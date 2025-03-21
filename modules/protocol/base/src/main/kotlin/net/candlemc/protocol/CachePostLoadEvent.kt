package net.candlemc.protocol

import net.candlemc.event.Event
import java.io.File

/**
 * Event triggered after the lookup cache has been successfully loaded from disk.
 *
 * <p>This event is published following the successful deserialization and validation of the lookup cache. Subscribers may
 * use this event to confirm that the cache load operation completed successfully and to initialize further components that depend on the cache.</p>
 *
 * @property registry the PacketRegistry whose cache has been loaded.
 * @property cacheFile the File object representing the source from which the cache was loaded.
 */
data class CachePostLoadEvent(val registry: PacketRegistry, val cacheFile: File) : Event<PacketRegistry>(registry)
