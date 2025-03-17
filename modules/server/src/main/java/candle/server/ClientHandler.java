package candle.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ClientHandler implements Runnable {
  private final Socket socket;
  private final MinecraftServer server;
  private InputStream in;
  private OutputStream out;

  public ClientHandler( Socket socket, MinecraftServer server ) {
    this.socket = socket;
    this.server = server;
  }

  @Override
  public void run() {
    String clientAddress = socket.getInetAddress().getHostAddress();
    System.out.println("New connection from " + clientAddress);
    try {
      in = socket.getInputStream();
      out = socket.getOutputStream();

      // Handshake verarbeiten
      int packetLength = readVarInt();
      int packetId = readVarInt();
      if ( packetId != 0x00 ) {
        System.err.println("Unexpected packet ID in handshake: " + packetId);
        return;
      }
      int protocolVersion = readVarInt();
      String serverAddress = readString();
      int serverPort = readUnsignedShort();
      int nextState = readVarInt();
      System.out.println("Handshake from " + clientAddress +
                         ": protocol=" + protocolVersion +
                         ", nextState=" + nextState);

      if ( protocolVersion != 769 ) {
        if ( nextState == 2 ) {
          sendDisconnect("Outdated client! Please use 1.21.4");
        }
        if ( nextState == 1 ) {
          handleStatus(); // sendet Status auch bei inkompatibler Version
        }
        return;
      }

      if ( nextState == 1 ) {
        handleStatus();
      } else if ( nextState == 2 ) {
        handleLogin();
      }
    } catch ( IOException e ) {
      System.err.println("Connection error: " + e.getMessage());
    } finally {
      try {
        socket.close();
      } catch ( IOException ignore ) {}
      System.out.println("Connection with " + clientAddress + " closed.");
    }
  }

  private void handleStatus() throws
                              IOException {
    // Lese Status Request (Packet ID 0, keine Daten)
    int length = readVarInt();
    int requestId = readVarInt();
    if ( requestId != 0x00 ) {
      System.err.println("Unexpected packet in status phase: " + requestId);
      return;
    }
    String statusJson = buildStatusJson();
    sendStatusResponse(statusJson);

    // Lese den Ping und antworte mit Pong
    int pingLength = readVarInt();
    int pingId = readVarInt();
    if ( pingId == 0x01 ) {
      long payload = readLong();
      sendPong(payload);
    }
  }

  private void handleLogin() throws
                             IOException {
    int length = readVarInt();
    int loginId = readVarInt();
    if ( loginId != 0x00 ) {
      System.err.println("Unexpected packet in login phase: " + loginId);
      return;
    }
    String playerName = readString();
    System.out.println("Player '" + playerName + "' is logging in...");

    UUID uuid = UUID.nameUUIDFromBytes(( "OfflinePlayer:" + playerName ).getBytes(StandardCharsets.UTF_8));
    sendLoginSuccess(uuid, playerName);
    server.onlineCount.incrementAndGet();
    System.out.println(playerName + " joined the server. Online count: " + server.onlineCount.get());

    try {
      while ( true ) {
        int data = in.read();
        if ( data == -1 ) {
          break;
        }
      }
    } finally {
      server.onlineCount.decrementAndGet();
      System.out.println(playerName + " left the server. Online count: " + server.onlineCount.get());
    }
  }

  /**
   * Baut den JSON-String für die Status-Antwort zusammen.
   */
  private String buildStatusJson() {
    String motd = server.getMotd();
    int maxPlayers = server.getMaxPlayers();
    int onlinePlayers = server.onlineCount.get();
    return String.format(
            "{" +
            "\"version\":{\"name\":\"1.21.4\",\"protocol\":769}," +
            "\"players\":{\"max\":%d,\"online\":%d}," +
            "\"description\":{\"text\":\"%s\"}" +
            "}",
            maxPlayers, onlinePlayers, motd
                        );
  }

  // --- Paket-Sende-Methoden mit neuem Hilfsmechanismus ---

  private void sendStatusResponse( String jsonPayload ) throws
                                                        IOException {
    ByteArrayOutputStream payloadBuffer = new ByteArrayOutputStream();
    writeString(payloadBuffer, jsonPayload);
    sendPacket(0x00, payloadBuffer);
  }

  private void sendPong( long payload ) throws
                                        IOException {
    ByteArrayOutputStream payloadBuffer = new ByteArrayOutputStream();
    writeLong(payloadBuffer, payload);
    sendPacket(0x01, payloadBuffer);
  }

  private void sendLoginSuccess( UUID uuid, String username ) throws IOException {
    ByteArrayOutputStream payloadBuffer = new ByteArrayOutputStream();

    // Schreibe UUID als 16-Byte-Wert
    writeUUID(payloadBuffer, uuid);
    writeString(payloadBuffer, username);

    // Leere Property-Liste senden (falls erwartet)
    writeVarInt(payloadBuffer, 0);

    sendPacket(0x02, payloadBuffer);
  }

  private void writeUUID(OutputStream output, UUID uuid) throws IOException {
    ByteArrayOutputStream uuidBuffer = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(uuidBuffer);
    dataOut.writeLong(uuid.getMostSignificantBits());
    dataOut.writeLong(uuid.getLeastSignificantBits());
    output.write(uuidBuffer.toByteArray());
  }

  private void sendDisconnect( String message ) throws
                                                IOException {
    ByteArrayOutputStream payloadBuffer = new ByteArrayOutputStream();
    String jsonMsg = String.format("{\"text\":\"%s\"}", message);
    writeString(payloadBuffer, jsonMsg);
    sendPacket(0x00, payloadBuffer);
  }

  /**
   * Hilfsmethode zum Senden eines vollständigen Pakets.
   */
  private void sendPacket( int packetId, ByteArrayOutputStream payloadBuffer ) throws
                                                                               IOException {
    ByteArrayOutputStream packetBuffer = new ByteArrayOutputStream();
    writeVarInt(packetBuffer, packetId);           // Write packet ID
    payloadBuffer.writeTo(packetBuffer);             // Append payload data
    ByteArrayOutputStream prefixedPacketBuffer = new ByteArrayOutputStream();
    writeVarInt(prefixedPacketBuffer, packetBuffer.size());
    packetBuffer.writeTo(prefixedPacketBuffer);
    byte[] packetData = prefixedPacketBuffer.toByteArray();
    // Debug: print hex dump of the packet data
    System.out.println("Sending packet with ID " + packetId + ": " + bytesToHex(packetData));

    // Write the full packet length first
    out.write(packetData);
    out.flush();
  }

  private String bytesToHex( byte[] bytes ) {
    StringBuilder sb = new StringBuilder();
    for ( byte b : bytes ) {
      sb.append(String.format("%02X ", b));
    }
    return sb.toString();
  }

  // --- Angepasste Schreibmethoden für einen beliebigen OutputStream ---

  private void writeVarInt( OutputStream output, int value ) throws
                                                             IOException {
    while ( ( value & ~0x7F ) != 0 ) {
      output.write(( value & 0x7F ) | 0x80);
      value >>>= 7;
    }
    output.write(value);
  }

  /**
   * Schreibt einen String (UTF-8) inkl. VarInt-Längenpräfix in einen OutputStream.
   */
  private void writeString( OutputStream output, String str ) throws
                                                              IOException {
    byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
    writeVarInt(output, bytes.length);
    output.write(bytes);
  }

  /**
   * Schreibt einen 8-Byte Long (Big-Endian) in einen OutputStream.
   */
  private void writeLong( OutputStream output, long value ) throws
                                                            IOException {
    DataOutputStream dataOut = new DataOutputStream(output);
    dataOut.writeLong(value);
  }

  // --- Bestehende Lese-Methoden (unverändert) ---

  private int readVarInt() throws
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
    return result;
  }

  private String readString() throws
                              IOException {
    int length = readVarInt();
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

  private int readUnsignedShort() throws
                                  IOException {
    int hi = in.read();
    int lo = in.read();
    if ( lo == -1 ) {
      throw new IOException("Stream ended while reading unsigned short");
    }
    return ( ( hi & 0xFF ) << 8 ) | ( lo & 0xFF );
  }

  private long readLong() throws
                          IOException {
    DataInputStream dataIn = new DataInputStream(in);
    return dataIn.readLong();
  }
}
