package candle.protocol.codec;

import candle.types.resource.NamespacedObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class TypeCodec<T> implements NamespacedObject {
  abstract T read( InputStream in, AbstractDataCodec dataCodec ) throws
                                                                 IOException;

  abstract void write( OutputStream out, T value, AbstractDataCodec dataCodec ) throws
                                                                                IOException;

  public TypeIdentifier<T> identifier( String namespacedIdentifier ) {
    String[] splitIdentifier = namespacedIdentifier.split(":", 1);
    return new TypeIdentifier<>(splitIdentifier[0], splitIdentifier[1], this);
  }

  public TypeIdentifier<T> identifier( String namespace, String identifier ) {
    return new TypeIdentifier<>(namespace, identifier, this);
  }
}
