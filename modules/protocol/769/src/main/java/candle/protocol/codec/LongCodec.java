package candle.protocol.codec;

import candle.types.primitives.Int64;
import candle.types.resource.Identifier;

import java.io.*;

public class LongCodec extends TypeCodec<Int64> {
  private static TypeIdentifier<Int64> IDENTIFIER;

  public LongCodec() {
    if ( IDENTIFIER == null ) {
      IDENTIFIER = identifier("minecraft", "long");
    }
  }

  @Override
  Int64 read( InputStream in, AbstractDataCodec dataCodec ) throws
                                                            IOException {
    DataInputStream dataIn = new DataInputStream(in);
    return new Int64(dataIn.readLong());
  }

  @Override
  void write( OutputStream out, Int64 value, AbstractDataCodec dataCodec ) throws
                                                                           IOException {
    DataOutputStream dataOut = new DataOutputStream(out);
    dataOut.writeLong(value.value());
  }

  @Override
  public Identifier namespacedIdentifier() {
    return null;
  }
}
