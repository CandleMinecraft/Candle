package net.candlemc.protocol.handshake.client

import net.candlemc.protocol.ConnectionState
import net.candlemc.protocol.Packet769
import net.candlemc.protocol.codec.AbstractDataCodec
import net.candlemc.protocol.codec.StringCodec
import net.candlemc.protocol.codec.UnsignedShortCodec
import net.candlemc.protocol.codec.VarIntCodec
import java.io.ByteArrayOutputStream
import java.io.InputStream

class HandshakePacket(
    val protocolVersion: Int,
    val serverAddress: String,
    val serverPort: UShort,
    val nextState: ConnectionState,
    codec: AbstractDataCodec,
) : Packet769(
    0x00u,
    serializeHandshakeData(protocolVersion, serverAddress, serverPort, nextState, codec),
    ConnectionState.HANDSHAKING
) {

    companion object {
        const val NEXT_STATE_STATUS = 1
        const val NEXT_STATE_LOGIN = 2
        const val NEXT_STATE_TRANSFER = 3

        @Throws(IllegalStateException::class)
        fun mapConnectionStateToInt(state: ConnectionState): Int = when (state) {
            ConnectionState.STATUS -> NEXT_STATE_STATUS
            ConnectionState.LOGIN -> NEXT_STATE_LOGIN
            ConnectionState.TRANSFER -> NEXT_STATE_TRANSFER
            else -> throw IllegalStateException("Connection state $state is not supported in Handshake packet")
        }

        @Throws(IllegalStateException::class)
        fun mapIntToConnectionState(value: Int): ConnectionState = when (value) {
            NEXT_STATE_STATUS -> ConnectionState.STATUS
            NEXT_STATE_LOGIN -> ConnectionState.LOGIN
            NEXT_STATE_TRANSFER -> ConnectionState.TRANSFER
            else -> throw IllegalStateException("Unsupported connection state: $value")
        }

        fun serializeHandshakeData(
            protocolVersion: Int,
            serverAddress: String,
            serverPort: UShort,
            nextState: ConnectionState,
            codec: AbstractDataCodec
        ): ByteArray {
            val outputStream = ByteArrayOutputStream()
            codec.writeType(outputStream, VarIntCodec.identifier(), protocolVersion)
            codec.writeType(outputStream, StringCodec.identifier(), serverAddress)
            codec.writeType(outputStream, UnsignedShortCodec.identifier(), serverPort)
            codec.writeType(outputStream, VarIntCodec.identifier(), mapConnectionStateToInt(nextState))
            return outputStream.toByteArray()
        }

        fun deserializeHandshakePacket(
            inputStream: InputStream,
            codec: AbstractDataCodec
        ): HandshakePacket {
            val protocolVersion = codec.readType(inputStream, VarIntCodec.identifier())
            val serverAddress = codec.readType(inputStream, StringCodec.identifier())
            val serverPort = codec.readType(inputStream, UnsignedShortCodec.identifier())
            val stateInt = codec.readType(inputStream, VarIntCodec.identifier())
            val connectionState = mapIntToConnectionState(stateInt)
            return HandshakePacket(protocolVersion, serverAddress, serverPort, connectionState, codec)
        }
    }
}
