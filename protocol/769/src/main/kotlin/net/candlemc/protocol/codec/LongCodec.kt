package net.candlemc.protocol.codec

import net.candlemc.types.resource.Identifier
import java.io.*

class LongCodec : TypeCodec<Long>() {
    init {
        if (IDENTIFIER == null) {
            IDENTIFIER = identifier("minecraft", "long")
        }
    }

    @Throws(IOException::class)
    override fun read(inputStream: InputStream, dataCodec: AbstractDataCodec): Long {
        val dataIn = DataInputStream(inputStream)
        return dataIn.readLong()
    }

    @Throws(IOException::class)
    override fun write(outputStream: OutputStream, value: Long, dataCodec: AbstractDataCodec) {
        val dataOut = DataOutputStream(outputStream)
        dataOut.writeLong(value)
    }

    override fun namespacedIdentifier(): Identifier {
        return IDENTIFIER!!
    }

    companion object {
        private var IDENTIFIER: TypeIdentifier<Long>? = null

        fun identifier(): TypeIdentifier<Long>? {
            return IDENTIFIER
        }
    }
}
