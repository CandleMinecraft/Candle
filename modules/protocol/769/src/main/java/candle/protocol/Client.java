package candle.protocol;

import candle.protocol.handshake.client.HandshakePacket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client implements Runnable {
  private final Socket socket;
  private InputStream in;
  private OutputStream out;

  public Client( Socket socket ) {
    this.socket = socket;
  }

  @Override
  public void run() {
    String clientAddress = socket.getInetAddress().getHostAddress();
    try {
      in = socket.getInputStream();
      out = socket.getOutputStream();
      HandshakePacket handshake = new HandshakePacket(in);
      System.out.printf("Handshake from: %s protocol=%s, nextState=%s%n", clientAddress, handshake.getProtocolVersion(),
                        handshake.getNextState());
    } catch ( IOException e ) {
      throw new RuntimeException(e);
    } finally {
      try {
        socket.close();
      } catch ( IOException ignore ) {}
    }
  }
}
