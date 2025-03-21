package net.candlemc.protocol.codec

import net.candlemc.types.resource.Identifier

object MinecraftDataCodec769 : AbstractDataCodec(Identifier.of("candlemc", "codec/769")) {
    val VAR_INT_CODEC: TypeCodec<*> = register(VarIntCodec())!!
    val STRING_CODEC: TypeCodec<*> = register(StringCodec())!!
    val UNSIGNED_SHORT_CODEC: TypeCodec<*> = register(UnsignedShortCodec())!!
    val UUID_CODEC: TypeCodec<*> = register(UUIDCodec())!!
    val LONG_CODEC: TypeCodec<*> = register(LongCodec())!!
}
