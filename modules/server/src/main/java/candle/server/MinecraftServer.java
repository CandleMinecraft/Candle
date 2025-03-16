package candle.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MinecraftServer {
  // Zähler für verbundene Spieler
  protected final AtomicInteger onlineCount = new AtomicInteger(0);
  // Server-Einstellungen
  private final int port;
  private final String motd;
  private final int maxPlayers;
  // Thread-Pool für Client-Handler
  private final ExecutorService threadPool = Executors.newCachedThreadPool();

  public MinecraftServer( int port, String motd, int maxPlayers ) {
    this.port = port;
    this.motd = motd;
    this.maxPlayers = maxPlayers;
  }

  public static void main( String[] args ) {
    // Standard-Serverstart auf Port 25565 mit MOTD und max. 20 Spielern
    MinecraftServer server = new MinecraftServer(25565, "§aMein Minecraft-Server", 20);
    server.start();
  }

  public void start() {
    System.out.println("Starting MinecraftServer on port " + port + " ...");
    try ( ServerSocket serverSocket = new ServerSocket(port) ) {
      System.out.println("Server is running. Waiting for connections...");
      // Endlosschleife zum Akzeptieren neuer Verbindungen
      while ( true ) {
        Socket clientSocket = serverSocket.accept();
        // Neue Verbindung -> ClientHandler in separatem Thread starten
        ClientHandler handler = new ClientHandler(clientSocket, this);
        threadPool.execute(handler);
      }
    } catch ( IOException e ) {
      System.err.println("Server error: " + e.getMessage());
    } finally {
      threadPool.shutdown();
    }
  }

  // Getter für MOTD und MaxPlayers (für Zugriff im ClientHandler)
  protected String getMotd() {
    return motd;
  }

  protected int getMaxPlayers() {
    return maxPlayers;
  }
}
