package net.candlemc.protocol

import net.candlemc.event.Event
import java.io.File

/**
 * Event triggered after the lookup cache has been successfully saved to disk.
 *
 * <p>This event is published following the successful serialization and storage of the lookup cache. Listeners can use this
 * event to confirm that the cache persistence operation was completed successfully and to trigger any follow-up processing.</p>
 *
 * @property registry the PacketRegistry whose cache has been saved.
 * @property cacheFile the File object representing the destination where the cache was stored.
 */
data class CachePostSaveEvent(val registry: PacketRegistry, val cacheFile: File) : Event<PacketRegistry>(registry)
