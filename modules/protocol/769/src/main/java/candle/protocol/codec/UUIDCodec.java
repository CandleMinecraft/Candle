package candle.protocol.codec;

import candle.types.ex.NotImplementedException;
import candle.types.resource.Identifier;

import java.io.*;
import java.util.UUID;

public class UUIDCodec extends TypeCodec<UUID> {
  private static TypeIdentifier<UUID> IDENTIFIER;

  public UUIDCodec() {
    if ( IDENTIFIER == null ) {
      IDENTIFIER = identifier("minecraft", "uuid");
    }
  }

  @Override
  UUID read( InputStream in, AbstractDataCodec codec ) throws
                                                       IOException,
                                                       NotImplementedException {
    throw new NotImplementedException("This functionality is not yet implemented!");
  }


  @Override
  void write( OutputStream out, UUID value, AbstractDataCodec dataCodec ) throws
                                                                          IOException {
    ByteArrayOutputStream uuidBuffer = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(uuidBuffer);
    dataOut.writeLong(value.getMostSignificantBits());
    dataOut.writeLong(value.getLeastSignificantBits());
    out.write(uuidBuffer.toByteArray());
  }

  @Override
  public Identifier namespacedIdentifier() {
    return IDENTIFIER;
  }
}
