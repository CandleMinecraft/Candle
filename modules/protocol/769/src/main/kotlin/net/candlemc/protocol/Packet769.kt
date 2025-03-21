package net.candlemc.protocol

import net.candlemc.protocol.codec.AbstractDataCodec
import net.candlemc.protocol.codec.VarIntCodec
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

open class Packet769(
    override val id: UByte,
    override val data: ByteArray,
    override val connectionState: ConnectionState
) : Packet {

    override fun length(): Int {
        return VarIntCodec.sizeOf(id.toInt()) + data.size
    }

    fun write(outputStream: OutputStream, dataCodec: AbstractDataCodec) {
        dataCodec.writeType(outputStream, VarIntCodec.identifier(), length())
        dataCodec.writeType(outputStream, VarIntCodec.identifier(), id.toInt())
        outputStream.write(data)
    }

    companion object {
        /**
         * Reads a packet from the input stream, using the registry to look up the correct packet type.
         *
         * @param inputStream the stream to read from
         * @param dataCodec the codec to read data types
         * @param connectionState the current connection state
         * @param protocolVersion the protocol version (used to select the proper PacketRegistry)
         * @return a deserialized Packet769 instance
         */
        @Throws(IOException::class)
        fun read(
            inputStream: InputStream,
            dataCodec: AbstractDataCodec,
            connectionState: ConnectionState,
            protocolVersion: Int
        ): Packet {
            // Read the length and packet id
            val length: Int = dataCodec.readType(inputStream, VarIntCodec.identifier())
            val packetIdInt = dataCodec.readType(inputStream, VarIntCodec.identifier())
            val packetId = packetIdInt.toUByte()

            // Read the rest of the packet data
            val packetData = ByteArray(length)
            var readBytes = 0
            while (readBytes < length) {
                val res = inputStream.read(packetData, readBytes, length - readBytes)
                if (res == -1) {
                    throw IOException("Stream ended while reading Packet")
                }
                readBytes += res
            }

            // Look up the packet prototype/factory from the registry.
            val registry = PacketRegistries.getRegistryByProtocol(protocolVersion)
                ?: throw IllegalStateException("No PacketRegistry for protocol version $protocolVersion")
            val packetKey = PacketKey(connectionState, packetId)
            val prototype = registry.getPacketFactory(packetKey)
                ?: throw IllegalStateException("No registered packet for key $packetKey")

            // Use the prototype’s deserialization method to create a new instance.
            return prototype.deserialize(packetData, dataCodec)
        }
    }
}
