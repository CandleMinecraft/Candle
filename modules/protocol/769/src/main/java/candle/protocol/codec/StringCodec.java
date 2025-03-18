package candle.protocol.codec;

import candle.types.primitives.Int32;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class StringCodec extends TypeCodec<String> {
  private static TypeIdentifier<String> IDENTIFIER;

  public StringCodec() {
    if ( IDENTIFIER == null ) {
      IDENTIFIER = identifier("minecraft", "string");
    }
  }

  public static TypeIdentifier<String> identifier() {
    return IDENTIFIER;
  }

  @Override
  public String read( InputStream in, AbstractDataCodec dataCodec ) throws
                                                                    IOException {
    int length = dataCodec.readType(in, VarIntCodec.identifier()).value();
    byte[] bytes = new byte[length];
    int readBytes = 0;
    while ( readBytes < length ) {
      int res = in.read(bytes, readBytes, length - readBytes);
      if ( res == -1 ) {
        throw new IOException("Stream ended while reading String");
      }
      readBytes += res;
    }
    return new String(bytes, StandardCharsets.UTF_8);
  }

  @Override
  public void write( OutputStream out, String value, AbstractDataCodec dataCodec ) throws
                                                                                   IOException {
    byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
    dataCodec.writeType(out, VarIntCodec.identifier(), new Int32(bytes.length));
    out.write(bytes);
  }

  @Override
  public candle.types.resource.Identifier namespacedIdentifier() {
    return identifier();
  }
}
