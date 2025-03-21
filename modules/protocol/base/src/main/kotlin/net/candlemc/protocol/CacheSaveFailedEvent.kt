package net.candlemc.protocol

import net.candlemc.event.Event
import java.io.File

/**
 * Event triggered when an error occurs during the cache saving process.
 *
 * <p>This event is published if an IOException or similar error occurs while saving the lookup cache. The event provides details
 * about the registry, the target cache file, and the exception encountered, enabling listeners to perform error handling or recovery.</p>
 *
 * @property registry the PacketRegistry whose cache saving operation failed.
 * @property cacheFile the File object representing the intended destination of the cache.
 * @property exception the Exception thrown during the cache save operation.
 */
data class CacheSaveFailedEvent(val registry: PacketRegistry, val cacheFile: File, val exception: Exception) :
    Event<PacketRegistry>(registry)
