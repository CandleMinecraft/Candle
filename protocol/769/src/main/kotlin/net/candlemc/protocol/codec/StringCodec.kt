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
        val length: Int = dataCodec.readType(inputStream, VarIntCodec.identifier()).value()
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
        val bytes = value.toByteArray(StandardCharsets.UTF_8)
        // Pass the Kotlin Int directly instead of Int32
        dataCodec.writeType(outputStream, VarIntCodec.identifier(), bytes.size)
        outputStream.write(bytes)
    }

    override fun namespacedIdentifier(): Identifier? {
        return identifier()
    }

    companion object {
        private var IDENTIFIER: TypeIdentifier<String>? = null

        fun identifier(): TypeIdentifier<String>? {
            return IDENTIFIER
        }
    }
}