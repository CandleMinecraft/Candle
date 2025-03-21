package net.candlemc.protocol

import net.candlemc.event.Event

/**
 * Event triggered before a packet is removed from a registry.
 *
 * <p>This event is published prior to the removal of a packet. It provides listeners with an opportunity to perform any
 * necessary pre-removal actions such as resource cleanup or validation. The event includes the registry and the unique key of the packet to be removed.</p>
 *
 * @property registry the PacketRegistry from which the packet is to be removed.
 * @property key the unique PacketKey that identifies the packet to be removed.
 */
data class PacketPreRemoveEvent(val registry: PacketRegistry, val key: PacketKey) : Event<PacketRegistry>(registry)
