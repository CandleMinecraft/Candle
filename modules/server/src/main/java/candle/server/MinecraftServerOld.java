package candle.server;

import candle.logger.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MinecraftServerOld {
  // Zähler für verbundene Spieler
  protected final AtomicInteger onlineCount = new AtomicInteger(0);
  // Server-Einstellungen
  private final int port;
  private final String motd;
  private final int maxPlayers;
  // Thread-Pool für Client-Handler
  private final ExecutorService threadPool = Executors.newCachedThreadPool();
  private final boolean debug;
  private final Logger logger;

  public MinecraftServerOld( int port, String motd, int maxPlayers, boolean debug ) throws
                                                                                    IOException {
    this.port = port;
    this.motd = motd;
    this.maxPlayers = maxPlayers;
    this.debug = debug;
    this.logger = new Logger(true);
  }

  public static void main( String[] argsArray ) throws
                                                IOException {
    List<String> args = Arrays.asList(argsArray);

    // Standard-Serverstart auf Port 25565 mit MOTD und max. 20 Spielern
    MinecraftServerOld server = new MinecraftServerOld(25565, "§aCandleMC§r §8§l»§f The most modern Server Software!",
                                                       20,
                                                       args.contains("--debug"));
    server.start();
  }

  public void start() {
    logger.info("Starting MinecraftServer on port " + port + " ...");
    try ( ServerSocket serverSocket = new ServerSocket(port) ) {
      logger.info("Server is running. Waiting for connections...");
      // Endlosschleife zum Akzeptieren neuer Verbindungen
      while ( true ) {
        Socket clientSocket = serverSocket.accept();
        // Neue Verbindung -> ClientHandler in separatem Thread starten
        ClientHandler handler = new ClientHandler(clientSocket, this, logger);
        threadPool.execute(handler);
      }
    } catch ( IOException e ) {
      logger.error("Server error: " + e.getMessage());
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

  protected boolean isDebug() {
    return debug;
  }
}