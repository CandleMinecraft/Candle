package candle.protocol.codec;

import candle.types.resource.Identifier;

public class TypeIdentifier<T> extends Identifier {
  private final TypeCodec<T> typeCodec;

  public TypeIdentifier( String namespace, String path, TypeCodec<T> typeCodec ) {
    super(namespace, path);
    this.typeCodec = typeCodec;
  }
}