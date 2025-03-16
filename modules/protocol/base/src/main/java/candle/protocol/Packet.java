package candle.protocol;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Getter
@EqualsAndHashCode
@ToString
public class Packet {
  private final int length;
  private final int id;
  private final ConnectionState state;
  private final byte[] data;

  public Packet(InputStream in, ConnectionState state) throws
                                IOException {
    this.state = state;
    this.length = readVarInt(in);
    this.id = readVarInt(in);
    int idLength = (Integer.toBinaryString(id).length() + 7) / 8;
    data = in.readNBytes(this.length - idLength);
  }

  public Packet(int length, int id, ConnectionState state, byte[] data) {
    this.length = length;
    this.id = id;
    this.state = state;
    this.data = data;
  }

  public static void writeVarInt( OutputStream output, int value ) throws
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
  public static void writeString( OutputStream output, String str ) throws
                                                              IOException {
    byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
    writeVarInt(output, bytes.length);
    output.write(bytes);
  }

  /**
   * Schreibt einen 8-Byte Long (Big-Endian) in einen OutputStream.
   */
  public static void writeLong( OutputStream output, long value ) throws
                                                            IOException {
    DataOutputStream dataOut = new DataOutputStream(output);
    dataOut.writeLong(value);
  }

  public static int readVarInt( InputStream in ) throws
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

  public static String readString(InputStream in) throws
                              IOException {
    int length = readVarInt(in);
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

  public static int readUnsignedShort(InputStream in) throws
                                  IOException {
    int hi = in.read();
    int lo = in.read();
    if ( lo == -1 ) {
      throw new IOException("Stream ended while reading unsigned short");
    }
    return ( ( hi & 0xFF ) << 8 ) | ( lo & 0xFF );
  }

  private long readLong(InputStream in) throws
                          IOException {
    DataInputStream dataIn = new DataInputStream(in);
    return dataIn.readLong();
  }

  public static String bytesToHex( byte[] bytes ) {
    StringBuilder sb = new StringBuilder();
    for ( byte b : bytes ) {
      sb.append(String.format("%02X ", b));
    }
    return sb.toString();
  }
}
