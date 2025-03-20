package net.candlemc.protocol.codec

import net.candlemc.types.exceptions.NotImplementedException
import net.candlemc.types.resource.Identifier
import java.io.*
import java.util.*

class UUIDCodec : TypeCodec<UUID>() {
    init {
        if (IDENTIFIER == null) {
            IDENTIFIER = identifier("minecraft", "uuid")
        }
    }

    @Throws(IOException::class, NotImplementedException::class)
    override fun read(inputStream: InputStream, dataCodec: AbstractDataCodec): UUID {
        val dataIn = DataInputStream(inputStream)
        val mostSignificantBits = dataIn.readLong()
        val leastSignificantBits = dataIn.readLong()
        if (mostSignificantBits == leastSignificantBits) {
            throw IOException("Invalid UUID data: most and least significant bits are identical")
        }
        return UUID(mostSignificantBits, leastSignificantBits)
    }


    @Throws(IOException::class)
    override fun write(outputStream: OutputStream, value: UUID, dataCodec: AbstractDataCodec) {
        val uuidBuffer = ByteArrayOutputStream()
        val dataOut = DataOutputStream(uuidBuffer)
        dataOut.writeLong(value.mostSignificantBits)
        dataOut.writeLong(value.leastSignificantBits)
        outputStream.write(uuidBuffer.toByteArray())
    }

    override fun namespacedIdentifier(): Identifier {
        return IDENTIFIER!!
    }

    companion object {
        private var IDENTIFIER: TypeIdentifier<UUID>? = null
    }
}
