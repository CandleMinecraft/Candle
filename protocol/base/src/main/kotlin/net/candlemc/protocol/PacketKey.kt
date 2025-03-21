package net.candlemc.protocol

import java.io.Serializable

/**
 * Represents a unique identifier for a packet based on its connection state and packet ID.
 *
 * <p>This class encapsulates the attributes that uniquely identify a packet across different protocol versions.
 * The connection state differentiates the operational context (such as HANDSHAKING, STATUS, LOGIN, etc.), while the
 * packet ID serves as the unique numeric identifier within that context.</p>
 *
 * @property connectionState the ConnectionState associated with the packet.
 * @property id the packet's unique identifier represented as an unsigned byte.
 */
data class PacketKey(val connectionState: ConnectionState, val id: UByte) : Serializable