package candle.server;

import candle.logger.Logger;
import candle.protocol.Client;
import candle.protocol.codec.AbstractDataCodec;
import candle.protocol.codec.MinecraftDataCodec769;
import candle.types.resource.Identifier;
import candle.types.resource.Registry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MinecraftServer {
  private final Logger logger = new Logger();
  private final ExecutorService threadPool = Executors.newCachedThreadPool();
  private Registry<AbstractDataCodec> dataCodecRegistry;
  private Registry<Registry<?>> registryRegistry;

  public MinecraftServer() {
    initRegistries();
  }

  public static void main( String[] args ) {
    MinecraftServer server = new MinecraftServer();
    server.start(25565);
  }

  private void initRegistries() {
    dataCodecRegistry = new Registry<>(Identifier.of("candlemc", "data_codec"));
    registryRegistry = new Registry<>(Identifier.of("candlemc", "registry"));
    registryRegistry.register(dataCodecRegistry);
    dataCodecRegistry.register(new MinecraftDataCodec769());
  }

  public void start( int port ) {
    logger.info("Starting MinecraftServer on port " + port + " ...");
    try ( ServerSocket serverSocket = new ServerSocket(port) ) {
      logger.info("Server is running. Waiting for connections...");
      while ( true ) {
        Socket clientSocket = serverSocket.accept();
        Client client = new Client(clientSocket);
        threadPool.execute(client);
      }
    } catch ( IOException e ) {
      logger.fatal("Error when starting the server!");
      logger.stacktrace(e);
    }
  }
}
