package net.candlemc.protocol.codec

import net.candlemc.protocol.codec.VarIntCodec.Companion
import net.candlemc.types.exceptions.NotImplementedException
import net.candlemc.types.resource.Identifier
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class UnsignedShortCodec : TypeCodec<UShort>() {
    init {
        if (IDENTIFIER == null) {
            IDENTIFIER = identifier("minecraft", "unsigned_short")
        }
    }

    @Throws(IOException::class)
    override fun read(inputStream: InputStream, dataCodec: AbstractDataCodec): UShort {
        val hi = inputStream.read()
        val lo = inputStream.read()
        if (hi == -1 || lo == -1) {
            throw IOException("Stream ended while reading unsigned short")
        }
        val value = ((hi and 0xFF) shl 8) or (lo and 0xFF)
        return value.toUShort()
    }


    @Throws(IOException::class, NotImplementedException::class)
    override fun write(outputStream: OutputStream, value: UShort, dataCodec: AbstractDataCodec) {
        throw NotImplementedException("This functionality is not yet implemented!")
    }

    override fun namespacedIdentifier(): Identifier {
        return IDENTIFIER!!
    }

    companion object {
        private var IDENTIFIER: TypeIdentifier<UShort>? = null

        fun identifier(): TypeIdentifier<UShort> {
            if (IDENTIFIER == null) throw IllegalStateException("UShortCodec was not initialized")
            return IDENTIFIER!!
        }
    }
}
