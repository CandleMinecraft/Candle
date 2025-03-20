package net.candlemc.protocol.handshake.client

import com.sun.tools.jconsole.JConsoleContext.ConnectionState
import java.io.ByteArrayInputStream

class HandshakePacket(inputStream: java.io.InputStream?) : Packet(inputStream, ConnectionState.HANDSHAKING) {
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
