package candle.protocol.codec;

import candle.types.resource.Identifier;

public class MinecraftDataCodec769 extends AbstractDataCodec {
  public final TypeCodec<?> VAR_INT_CODEC = register(new VarIntCodec());
  public final TypeCodec<?> STRING_CODEC = register(new StringCodec());
  public final TypeCodec<?> UNSIGNED_SHORT_CODEC = register(new UnsignedShortCodec());
  public final TypeCodec<?> UUID_CODEC = register(new UUIDCodec());
  public final TypeCodec<?> LONG_CODEC = register(new LongCodec());

  public MinecraftDataCodec769() {
    super(Identifier.of("candlemc", "codec/769"));
  }
}
