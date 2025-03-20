package net.candlemc.protocol.codec

import net.candlemc.types.resource.Identifier
import net.candlemc.types.resource.Registry
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@Suppress("UNCHECKED_CAST")
abstract class AbstractDataCodec(registryName: Identifier) : Registry<TypeCodec<*>?>(registryName) {

    @Throws(IOException::class)
    fun <T> writeType(outputStream: OutputStream, identifier: TypeIdentifier<T>, value: T) {
        val codec: TypeCodec<T> = get(identifier) as TypeCodec<T>?
            ?: throw IOException("No codec registered with identifier: $identifier")
        codec.write(outputStream, value, this)
    }

    @Throws(IOException::class)
    fun <T> readType(inputStream: InputStream, identifier: TypeIdentifier<T>): T {
        val codec: TypeCodec<T> = get(identifier) as TypeCodec<T>?
            ?: throw IOException("No codec registered with identifier: $identifier")
        return codec.read(inputStream, this)
    }
}
