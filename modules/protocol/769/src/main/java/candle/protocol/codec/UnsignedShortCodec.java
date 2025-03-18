package candle.protocol.codec;

import candle.types.ex.NotImplementedException;
import candle.types.primitives.UInt16;
import candle.types.resource.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UnsignedShortCodec extends TypeCodec<UInt16> {
  private static TypeIdentifier<UInt16> IDENTIFIER;

  public UnsignedShortCodec() {
    if ( IDENTIFIER == null ) {
      IDENTIFIER = identifier("minecraft", "unsigned_short");
    }
  }

  @Override
  UInt16 read( InputStream in, AbstractDataCodec codec ) throws
                                                         IOException {
    int hi = in.read();
    int lo = in.read();
    if ( hi == -1 || lo == -1 ) {
      throw new IOException("Stream ended while reading unsigned short");
    }
    int value = ( ( hi & 0xFF ) << 8 ) | ( lo & 0xFF );
    return new UInt16(value);
  }


  @Override
  void write( OutputStream out, UInt16 value, AbstractDataCodec dataCodec ) throws
                                                                            IOException,
                                                                            NotImplementedException {
    throw new NotImplementedException("This functionality is not yet implemented!");
  }

  @Override
  public Identifier namespacedIdentifier() {
    return IDENTIFIER;
  }
}
