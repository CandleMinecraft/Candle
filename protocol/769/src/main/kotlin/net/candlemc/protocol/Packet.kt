package net.candlemc.protocol

import net.candlemc.functions.TriFunction
import net.candlemc.protocol.codec.AbstractDataCodec
import net.candlemc.protocol.codec.VarIntCodec
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets

open class Packet(val packetId: UByte, val packetData: ByteArray, val connectionState: ConnectionState) {
    fun getPacketLength(): Int {
        return packetId.toInt() + packetData.size
    }

    fun write(outputStream: OutputStream, dataCodec: AbstractDataCodec) {
        val bytes = value.toByteArray(StandardCharsets.UTF_8)
        dataCodec.writeType(outputStream, VarIntCodec.identifier(), bytes.size)
        outputStream.write(bytes)
    }

    companion object {
        fun read(inputStream: InputStream, dataCodec: AbstractDataCodec, connectionState: ConnectionState, factory: TriFunction<UByte, ByteArray, ConnectionState, out Packet>): Packet {
            val length: Int = dataCodec.readType(inputStream, VarIntCodec.identifier())
            val packetId = dataCodec.readType(inputStream, VarIntCodec.identifier()).toUByte()
            val packetData = ByteArray(length)
            var readBytes = 0
            while (readBytes < length) {
                val res = inputStream.read(packetData, readBytes, length - readBytes)
                if (res == -1) {
                    throw IOException("Stream ended while reading Packet")
                }
                readBytes += res
            }

            return factory.apply(packetId, packetData, connectionState);
        }
    }
}
