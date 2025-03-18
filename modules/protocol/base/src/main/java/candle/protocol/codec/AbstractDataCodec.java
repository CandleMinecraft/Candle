package candle.protocol.codec;

import candle.types.resource.Identifier;
import candle.types.resource.Registry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractDataCodec extends Registry<TypeCodec<?>> {

  public AbstractDataCodec( Identifier registryName ) {
    super(registryName);
  }

  protected <T> void writeType( OutputStream out, TypeIdentifier<T> identifier, T value ) throws
                                                                                          IOException {
    @SuppressWarnings("unchecked")
    TypeCodec<T> codec = (TypeCodec<T>) get(identifier);
    if ( codec == null ) {
      throw new IOException("No codec registered with identifier: " + identifier);
    }
    codec.write(out, value, this);
  }

  protected <T> T readType( InputStream in, TypeIdentifier<T> identifier ) throws
                                                                           IOException {
    @SuppressWarnings("unchecked")
    TypeCodec<T> codec = (TypeCodec<T>) get(identifier);
    if ( codec == null ) {
      throw new IOException("No codec registered with identifier: " + identifier);
    }
    return codec.read(in, this);
  }
}
