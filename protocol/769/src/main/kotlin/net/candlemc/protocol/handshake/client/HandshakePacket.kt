package net.candlemc.protocol.handshake.client

import net.candlemc.protocol.ConnectionState
import net.candlemc.protocol.Packet
import java.io.ByteArrayInputStream

class HandshakePacket(packetId: UByte, packetData: ByteArray) : Packet(packetId, packetData, ConnectionState.HANDSHAKING) {
    private val protocolVersion: Int
    private val serverAddress: String
    private val serverPort: Int
    private val nextState: Int

    init {
        val dataIn: java.io.InputStream = ByteArrayInputStream(this.getData())
        protocolVersion = readVarInt(dataIn)
        serverAddress = readString(dataIn)
        serverPort = readUnsignedShort(dataIn)
        nextState = readVarInt(dataIn)
    }
}
