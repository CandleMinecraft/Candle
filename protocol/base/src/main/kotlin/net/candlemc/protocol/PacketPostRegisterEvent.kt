package net.candlemc.protocol

import net.candlemc.event.Event

/**
 * Event triggered after a packet has been successfully registered.
 *
 * <p>This event is published by the PacketRegistry after a packet is added. Subscribers can use this event to perform
 * post-registration actions such as auditing, metrics collection, or triggering additional workflows that depend on the new packet.</p>
 *
 * @property registry the PacketRegistry where the packet has been registered.
 * @property key the unique PacketKey that identifies the registered packet.
 * @property packet the Packet instance that was registered.
 */
data class PacketPostRegisterEvent(val registry: PacketRegistry, val key: PacketKey, val packet: Packet) :
    Event<PacketRegistry>(registry)
