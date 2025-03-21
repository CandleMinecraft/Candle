package net.candlemc.protocol

import net.candlemc.event.Event

/**
 * Event triggered after a packet has been removed from a registry.
 *
 * <p>This event is published after the packet has been successfully removed from the PacketRegistry. It enables listeners
 * to perform post-removal actions such as logging the removal or updating dependent systems.</p>
 *
 * @property registry the PacketRegistry from which the packet was removed.
 * @property key the unique PacketKey that identifies the removed packet.
 */
data class PacketPostRemoveEvent(val registry: PacketRegistry, val key: PacketKey) : Event<PacketRegistry>(registry)
