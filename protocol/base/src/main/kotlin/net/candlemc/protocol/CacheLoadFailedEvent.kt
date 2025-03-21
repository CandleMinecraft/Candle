package net.candlemc.protocol

import net.candlemc.event.Event
import java.io.File

/**
 * Event triggered when an error occurs during the cache loading process.
 *
 * <p>This event is dispatched if an exception is encountered during the deserialization or validation of the lookup cache.
 * The event provides the registry, source file, and exception details to facilitate error handling and debugging.</p>
 *
 * @property registry the PacketRegistry whose cache loading operation failed.
 * @property cacheFile the File object representing the source from which the cache was attempted to be loaded.
 * @property exception the Exception thrown during the cache load operation.
 */
data class CacheLoadFailedEvent(val registry: PacketRegistry, val cacheFile: File, val exception: Exception) :
    Event<PacketRegistry>(registry)
