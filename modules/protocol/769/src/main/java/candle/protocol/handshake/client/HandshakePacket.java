package candle.protocol.handshake.client;

import candle.protocol.ConnectionState;
import candle.protocol.Packet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class HandshakePacket extends Packet {
  private final int protocolVersion;
  private final String serverAddress;
  private final int serverPort;
  private final int nextState;

  public HandshakePacket( InputStream in ) throws
                                           IOException {
    super(in, ConnectionState.HANDSHAKING);
    InputStream dataIn = new ByteArrayInputStream(this.getData());
    protocolVersion = readVarInt(dataIn);
    serverAddress = readString(dataIn);
    serverPort = readUnsignedShort(dataIn);
    nextState = readVarInt(dataIn);
  }
}
