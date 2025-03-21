package net.candlemc.protocol

import net.candlemc.protocol.handshake.client.HandshakePacket

object Protocol769: PacketRegistry(769) {
    init {
        registerPacket(PacketKey(ConnectionState.HANDSHAKING, 0x00u), ::HandshakePacket)
        preGenerateCache()
    }
}