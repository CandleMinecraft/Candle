package net.candlemc.protocol

import net.candlemc.event.Event
import java.io.File

/**
 * Event triggered before the lookup cache is saved to disk.
 *
 * <p>This event is dispatched before the PacketRegistry attempts to persist its lookup cache. Subscribers may use this
 * event to perform validations, backups, or logging prior to the cache save operation.</p>
 *
 * @property registry the PacketRegistry whose cache is about to be saved.
 * @property cacheFile the File object representing the destination where the cache will be stored.
 */
data class CachePreSaveEvent(val registry: PacketRegistry, val cacheFile: File) : Event<PacketRegistry>(registry)
