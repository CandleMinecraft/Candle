package net.candlemc.protocol

import net.candlemc.event.Event

/**
 * Event triggered prior to the registration of a packet.
 *
 * <p>This event is published by the PacketRegistry before a packet is registered. Listeners can subscribe to this event
 * to perform pre-registration processing, such as validation or logging. The event contains a reference to the target registry
 * and the unique key that identifies the packet.</p>
 *
 * @property registry the PacketRegistry in which the packet is about to be registered.
 * @property key the unique PacketKey that identifies the packet.
 */
data class PacketPreRegisterEvent(val registry: PacketRegistry, val key: PacketKey) : Event<PacketRegistry>(registry)
