package net.candlemc.protocol.codec

import net.candlemc.types.resource.NamespacedObject
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

abstract class TypeCodec<T> : NamespacedObject {
    @Throws(IOException::class)
    abstract fun read(inputStream: InputStream, dataCodec: AbstractDataCodec): T

    @Throws(IOException::class)
    abstract fun write(outputStream: OutputStream, value: T, dataCodec: AbstractDataCodec)

    fun identifier(namespacedIdentifier: String): TypeIdentifier<T> {
        val splitIdentifier = namespacedIdentifier.split(":".toRegex(), limit = 1).toTypedArray()
        return TypeIdentifier<T>(splitIdentifier[0], splitIdentifier[1], this)
    }

    fun identifier(namespace: String, identifier: String): TypeIdentifier<T> {
        return TypeIdentifier<T>(namespace, identifier, this)
    }
}
