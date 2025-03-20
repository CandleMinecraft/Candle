package net.candlemc.protocol.codec

import net.candlemc.types.resource.Identifier
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VarIntCodec : TypeCodec<Int>() {
    init {
        if (IDENTIFIER == null) {
            IDENTIFIER = identifier("minecraft", "var_int")
        }
    }

    @Throws(IOException::class)
    override fun read(inputStream: InputStream, dataCodec: AbstractDataCodec): Int {
        var numRead = 0
        var result = 0
        var read: Byte
        do {
            val byteVal = inputStream.read()
            if (byteVal == -1) {
                throw IOException("Stream ended while reading VarInt")
            }
            read = byteVal.toByte()
            result = result or ((read.toInt() and 0x7F) shl (7 * numRead))
            numRead++
            if (numRead > 5) {
                throw IOException("VarInt too long")
            }
        } while ((read.toInt() and 0x80) != 0)
        return result
    }

    @Throws(IOException::class)
    override fun write(outputStream: OutputStream, value: Int, dataCodec: AbstractDataCodec) {
        var intValue: Int = value
        while ((intValue and 0x7F.inv()) != 0) {
            outputStream.write((intValue and 0x7F) or 0x80)
            intValue = intValue ushr 7
        }
        outputStream.write(intValue)
    }

    override fun namespacedIdentifier(): Identifier {
        return IDENTIFIER!!
    }

    companion object {
        private var IDENTIFIER: TypeIdentifier<Int>? = null

        fun identifier(): TypeIdentifier<Int> {
            if (IDENTIFIER == null) throw IllegalStateException("VarIntCodec was not initialized")
            return IDENTIFIER!!
        }
    }
}
