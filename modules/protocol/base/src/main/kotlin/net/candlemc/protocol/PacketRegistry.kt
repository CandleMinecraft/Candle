package net.candlemc.protocol

import net.candlemc.event.EventBus
import net.candlemc.functions.QuadFunction
import net.candlemc.logger.Logger
import net.candlemc.protocol.codec.AbstractDataCodec
import net.candlemc.types.resource.Identifier
import net.candlemc.types.resource.Registry
import java.io.*
import java.util.concurrent.ConcurrentHashMap

/**
 * A registry for managing packet instances with caching and event-driven notifications.
 *
 * <p>This class handles the registration, retrieval, and removal of packet instances. Each PacketRegistry is associated with a specific protocol version and is identified by <code>candlemc:packet_registry/&lt;version&gt;</code>.
 * It maintains a local storage map and a lookup cache for fast O(1) retrieval of packets. It supports cascading lookups through an optional parent registry, persistence of the lookup cache to disk with signature validation, and comprehensive event notifications via an EventBus for major operations.</p>
 *
 * @constructor Creates a PacketRegistry for a specific protocol version, with an optional parent registry for cascading lookups.
 * @param protocolVersion the protocol version associated with this registry.
 * @param parentRegistry an optional PacketRegistry that serves as a fallback for lookup operations.
 * @param registryIdentifier the unique identifier for this PacketRegistry, typically in the format <code>candlemc:packet_registry/&lt;version&gt;</code>.
 */
open class PacketRegistry(
    val protocolVersion: Int,
    private val parentRegistry: PacketRegistry? = null,
    registryIdentifier: Identifier = Identifier.of("candlemc", "packet_registry/$protocolVersion")
) : Registry<Packet>(registryIdentifier) {

    /**
     * Local storage mapping PacketKey to Packet instances for this registry.
     */
    internal val localRegistry: ConcurrentHashMap<PacketKey, QuadFunction<UByte, Int, ByteArray, AbstractDataCodec, out Packet>> = ConcurrentHashMap()

    /**
     * Lookup cache mapping PacketKey to CacheEntry for fast retrieval of packets.
     */
    private val lookupCache: ConcurrentHashMap<PacketKey, CacheEntry> = ConcurrentHashMap()

    /**
     * Registers a new packet into the registry.
     *
     * <p>This method performs the following operations:
     * <ol>
     *   <li>Validates that the packet is not already registered based on its unique PacketKey.</li>
     *   <li>Publishes a pre-registration event via the EventBus.</li>
     *   <li>Adds the packet to the local registry and updates the lookup cache accordingly.</li>
     *   <li>Publishes a post-registration event via the EventBus and logs the operation.</li>
     * </ol>
     * The registration is performed asynchronously via the EventBus, ensuring non-blocking execution.</p>
     *
     * @param key the Packet to be registered.
     * @return the registered Packet instance.
     * @throws IllegalArgumentException if a packet with the same PacketKey is already registered.
     */
    fun registerPacket(key: PacketKey, factory: QuadFunction<UByte, Int, ByteArray, AbstractDataCodec, out Packet>): PacketKey {
        if (localRegistry.containsKey(key)) {
            throw IllegalArgumentException("Packet already registered: $key")
        }
        eventBus.publish(PacketPreRegisterEvent(this, key)).thenRun {
            localRegistry[key] = factory
            updateCacheOnRegistration(key)
            eventBus.publish(PacketPostRegisterEvent(this, key, factory))
            logger.info("Packet registered: $key in protocol version $protocolVersion")
        }
        return key
    }

    /**
     * Removes a packet from the registry identified by its PacketKey.
     *
     * <p>This method performs the following operations:
     * <ol>
     *   <li>Checks if the packet exists in the local registry; if not, logs a warning and exits.</li>
     *   <li>Publishes a pre-removal event via the EventBus.</li>
     *   <li>Removes the packet from the local registry and updates the lookup cache.</li>
     *   <li>Publishes a post-removal event via the EventBus and logs the removal.</li>
     * </ol></p>
     *
     * @param key the unique PacketKey identifying the packet to be removed.
     */
    fun removePacket(key: PacketKey) {
        if (!localRegistry.containsKey(key)) {
            logger.warn("No packet found to remove: $key")
            return
        }
        eventBus.publish(PacketPreRemoveEvent(this, key)).thenRun {
            localRegistry.remove(key)
            updateCacheOnRemoval(key)
            eventBus.publish(PacketPostRemoveEvent(this, key))
            logger.info("Packet removed: $key in protocol version $protocolVersion")
        }
    }

    /**
     * Retrieves a packet from the registry using the lookup cache.
     *
     * <p>This method attempts to retrieve a packet by:
     * <ol>
     *   <li>Checking the lookup cache for an entry corresponding to the provided PacketKey.</li>
     *   <li>If the entry indicates a found packet, the packet is retrieved from the corresponding PacketRegistry based on the stored protocol version.</li>
     *   <li>If the entry indicates that the packet has been removed or no entry exists, {@code null} is returned.</li>
     * </ol></p>
     *
     * @param key the unique PacketKey identifying the packet.
     * @return the Packet instance if found, or {@code null} if not found or if the packet has been removed.
     */
    fun getPacketFactory(key: PacketKey): QuadFunction<UByte, Int, ByteArray, AbstractDataCodec, out Packet>? {
        return when (val entry = lookupCache[key]) {
            is CacheEntry.Found ->
                PacketRegistries.get(Identifier.of("candlemc", "packet_registry/${entry.protocolVersion}"))
                    ?.localRegistry?.get(key)

            CacheEntry.Removed, null -> null
        }
    }

    /**
     * Updates the lookup cache when a packet is registered.
     *
     * <p>This method creates a {@code Found} entry in the lookup cache for the given PacketKey using the current protocol version,
     * and propagates the update to any parent registries to ensure cascading consistency.</p>
     *
     * @param key the unique PacketKey of the registered packet.
     */
    private fun updateCacheOnRegistration(key: PacketKey) {
        lookupCache[key] = CacheEntry.Found(protocolVersion)
        parentRegistry?.propagateCacheUpdate(key, CacheEntry.Found(protocolVersion))
    }

    /**
     * Updates the lookup cache when a packet is removed.
     *
     * <p>This method sets a {@code Removed} entry in the lookup cache for the given PacketKey and propagates the removal
     * to parent registries to ensure that the packet is not returned in future lookups.</p>
     *
     * @param key the unique PacketKey of the packet to be removed.
     */
    private fun updateCacheOnRemoval(key: PacketKey) {
        lookupCache[key] = CacheEntry.Removed
        parentRegistry?.propagateCacheUpdate(key, CacheEntry.Removed)
    }

    /**
     * Propagates a cache update to parent registries when no local override exists.
     *
     * <p>This method ensures that cache updates (registration or removal) are propagated to all parent registries that do not have
     * a local entry for the PacketKey, maintaining consistency across cascading lookups.</p>
     *
     * @param key the unique PacketKey for which the cache update is being propagated.
     * @param entry the CacheEntry to propagate, indicating either a {@code Found} or {@code Removed} state.
     */
    private fun propagateCacheUpdate(key: PacketKey, entry: CacheEntry) {
        if (!localRegistry.containsKey(key)) {
            lookupCache[key] = entry
        }
        parentRegistry?.propagateCacheUpdate(key, entry)
    }

    /**
     * Pre-generates the lookup cache by aggregating entries from this registry and its parent registries.
     *
     * <p>This method clears the current lookup cache and rebuilds it by recursively invoking pre-generation on the parent registry,
     * then overlaying the local registrations. This process ensures that subsequent packet lookups are performed in O(1) time.</p>
     */
    fun preGenerateCache() {
        lookupCache.clear()
        parentRegistry?.let {
            it.preGenerateCache()
            lookupCache.putAll(it.lookupCache)
        }
        for (key in localRegistry.keys) {
            lookupCache[key] = CacheEntry.Found(protocolVersion)
        }
        logger.info("Lookup cache pre-generation complete for protocol version $protocolVersion")
    }

    /**
     * Computes a signature representing the current state of the registry.
     *
     * <p>This method constructs a signature string by concatenating the parent's signature (if present) with the current protocol version
     * and a list of all locally registered PacketKeys. This signature is used to validate the integrity of the lookup cache during persistence operations.</p>
     *
     * @return a String representing the computed signature of the registry state.
     */
    fun computeSignature(): String {
        val parentSig = parentRegistry?.computeSignature() ?: ""
        val localKeys = localRegistry.keys.joinToString(separator = ",") { it.toString() }
        return "$parentSig-$protocolVersion-$localKeys"
    }

    /**
     * Saves the lookup cache to disk along with its computed signature.
     *
     * <p>This method serializes the lookup cache and writes it to the specified file. It publishes pre-save and post-save events via the EventBus.
     * If an IOException occurs, the exception's stack trace is printed, an error is logged, and a cache save failure event is published.</p>
     *
     * @param cacheFile the File object representing the destination for the lookup cache.
     */
    fun saveCacheToDisk(cacheFile: File) {
        val signature = computeSignature()
        try {
            eventBus.publish(CachePreSaveEvent(this, cacheFile))
            ObjectOutputStream(FileOutputStream(cacheFile)).use { oos ->
                oos.writeObject(signature)
                oos.writeObject(HashMap(lookupCache))
            }
            logger.info("Cache saved successfully for protocol version $protocolVersion")
            eventBus.publish(CachePostSaveEvent(this, cacheFile))
        } catch (e: IOException) {
            logger.error("Cache saving failed for protocol version $protocolVersion: ${e.message}")
            e.printStackTrace()
            eventBus.publish(CacheSaveFailedEvent(this, cacheFile, e))
        }
    }

    /**
     * Loads the lookup cache from disk and validates it using the computed signature.
     *
     * <p>This method attempts to deserialize the lookup cache from the specified file. A pre-load event is published prior to loading.
     * After deserialization, the stored signature is compared with a freshly computed signature. If they do not match, the cache is deemed invalid,
     * and a cache load failure event is published. On successful loading and validation, a post-load event is published. In case of an exception,
     * the exception's stack trace is printed, an error is logged, and a cache load failure event is published.</p>
     *
     * @param cacheFile the File object representing the source of the lookup cache.
     */
    fun loadCacheFromDisk(cacheFile: File) {
        if (!cacheFile.exists()) {
            logger.info("No cache file found for protocol version $protocolVersion")
            return
        }
        try {
            eventBus.publish(CachePreLoadEvent(this, cacheFile))
            ObjectInputStream(FileInputStream(cacheFile)).use { ois ->
                val storedSignature = ois.readObject() as String
                if (storedSignature != computeSignature()) {
                    logger.info("Cache is invalid for protocol version $protocolVersion – signature mismatch")
                    eventBus.publish(CacheLoadFailedEvent(this, cacheFile, Exception("Signature mismatch")))
                    return
                }
                @Suppress("UNCHECKED_CAST")
                val storedCache = ois.readObject() as HashMap<PacketKey, CacheEntry>
                lookupCache.clear()
                lookupCache.putAll(storedCache)
                logger.info("Cache loaded successfully for protocol version $protocolVersion")
                eventBus.publish(CachePostLoadEvent(this, cacheFile))
            }
        } catch (e: Exception) {
            logger.error("Cache loading failed for protocol version $protocolVersion: ${e.message}")
            e.printStackTrace()
            eventBus.publish(CacheLoadFailedEvent(this, cacheFile, e))
        }
    }

    /**
     * Companion object providing global instances for the EventBus and Logger.
     *
     * <p>This companion object holds a shared EventBus instance for dispatching events related to packet and cache operations,
     * and a Logger instance configured for English language logging.</p>
     */
    companion object {
        /**
         * Global EventBus instance for event dispatching.
         */
        val eventBus = EventBus()

        /**
         * Global Logger instance configured for English language logging.
         */
        val logger = Logger(name = "PacketRegistry")
    }
}