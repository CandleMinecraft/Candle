package net.candlemc.protocol.codec

import net.candlemc.types.resource.Identifier
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets

class StringCodec : TypeCodec<String>() {
    init {
        if (IDENTIFIER == null) {
            IDENTIFIER = identifier("minecraft", "string")
        }
    }

    @Throws(IOException::class)
    override fun read(inputStream: InputStream, dataCodec: AbstractDataCodec): String {
        val length: Int = dataCodec.readType(inputStream, VarIntCodec.identifier())
        val bytes = ByteArray(length)
        var readBytes = 0
        while (readBytes < length) {
            val res = inputStream.read(bytes, readBytes, length - readBytes)
            if (res == -1) {
                throw IOException("Stream ended while reading String")
            }
            readBytes += res
        }
        return String(bytes, StandardCharsets.UTF_8)
    }

    @Throws(IOException::class)
    override fun write(outputStream: OutputStream, value: String, dataCodec: AbstractDataCodec) {
        val bytes: ByteArray = value.toByteArray(StandardCharsets.UTF_8)
        dataCodec.writeType(outputStream, VarIntCodec.identifier(), bytes.size)
        outputStream.write(bytes)
    }

    override fun namespacedIdentifier(): Identifier {
        return identifier()
    }

    companion object {
        private var IDENTIFIER: TypeIdentifier<String>? = null

        fun identifier(): TypeIdentifier<String> {
            if (IDENTIFIER == null) throw IllegalStateException("StringCodec was not initialized")
            return IDENTIFIER!!
        }
    }
}