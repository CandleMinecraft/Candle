package net.candlemc.protocol

import net.candlemc.event.Event
import java.io.File

/**
 * Event triggered before the lookup cache is loaded from disk.
 *
 * <p>This event is dispatched before the PacketRegistry attempts to load and deserialize its lookup cache from disk.
 * Listeners can use this event to prepare for the cache load, perform validations, or log the operation.</p>
 *
 * @property registry the PacketRegistry whose cache is about to be loaded.
 * @property cacheFile the File object representing the source from which the cache will be loaded.
 */
data class CachePreLoadEvent(val registry: PacketRegistry, val cacheFile: File) : Event<PacketRegistry>(registry)
