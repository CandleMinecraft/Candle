package candle.protocol.codec;

import candle.types.primitives.Int32;
import candle.types.resource.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class VarIntCodec extends TypeCodec<Int32> {
  private static TypeIdentifier<Int32> IDENTIFIER;

  public VarIntCodec() {
    if ( IDENTIFIER == null ) {
      IDENTIFIER = identifier("minecraft", "string");
    }
  }

  public static TypeIdentifier<Int32> identifier() {
    return IDENTIFIER;
  }

  @Override
  public Int32 read( InputStream in, AbstractDataCodec dataCodec ) throws
                                                                   IOException {
    int numRead = 0;
    int result = 0;
    byte read;
    do {
      int byteVal = in.read();
      if ( byteVal == -1 ) {
        throw new IOException("Stream ended while reading VarInt");
      }
      read = (byte) byteVal;
      result |= ( read & 0x7F ) << ( 7 * numRead );
      numRead++;
      if ( numRead > 5 ) {
        throw new IOException("VarInt too long");
      }
    } while ( ( read & 0x80 ) != 0 );
    return new Int32(result);
  }

  @Override
  public void write( OutputStream out, Int32 value, AbstractDataCodec dataCodec ) throws
                                                                                  IOException {
    int intValue = value.value();
    while ( ( intValue & ~0x7F ) != 0 ) {
      out.write(( intValue & 0x7F ) | 0x80);
      intValue >>>= 7;
    }
    out.write(intValue);
  }

  @Override
  public Identifier namespacedIdentifier() {
    return IDENTIFIER;
  }
}
