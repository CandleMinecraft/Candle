package net.candlemc.protocol

import net.candlemc.protocol.codec.AbstractDataCodec

interface Packet {
    val id: UByte
    fun length(): Int
    val data: ByteArray
    val connectionState: ConnectionState

    /**
     * Each concrete packet should implement how to deserialize itself from raw data.
     * This method creates a new instance from the given data.
     */
    fun deserialize(packetData: ByteArray, dataCodec: AbstractDataCodec): Packet {
        throw UnsupportedOperationException("deserialize must be overridden")
    }
}