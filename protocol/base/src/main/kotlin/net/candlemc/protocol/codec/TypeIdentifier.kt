package net.candlemc.protocol.codec

import net.candlemc.types.resource.Identifier

class TypeIdentifier<T>(namespace: String, path: String, private val typeCodec: TypeCodec<T>) :
    Identifier(namespace, path) 