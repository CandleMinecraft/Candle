package candle.logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Logger {
  // Single Thread Executor for all logging operations
  private static final ExecutorService executor = Executors.newSingleThreadExecutor();
  private static final LoggingStrategy debugStrategy = new CandleLoggingStrategy(
          "debug", "DEBUG", executor, ANSIColors.GREEN);
  private static final LoggingStrategy errorStrategy = new CandleLoggingStrategy(
          "error", "ERROR", executor, ANSIColors.RED);
  private static final LoggingStrategy fatalStrategy = new CandleLoggingStrategy(
          "error", "FATAL", executor, ANSIColors.BOLD_RED);
  private static final LoggingStrategy infoStrategy = new CandleLoggingStrategy(
          "info", "INFO", executor, ANSIColors.CYAN);
  private static final LoggingStrategy stackTraceStrategy = new StackTraceLoggingStrategy(executor);
  private static final LoggingStrategy warnStrategy = new CandleLoggingStrategy(
          "info", "WARN", executor, ANSIColors.YELLOW);
  private final String name; // Name of the Module, Plugin and so on.
  private final boolean debug; // Is the Logger in Debug Mode.

  public Logger() throws
                  IOException {
    this(false);
  }

  public Logger( boolean debug, String name ) throws
                                              IOException {
    this.name = name;
    this.debug = debug;

    Files.createDirectories(Path.of(System.getProperty("user.dir") + "/logs"));
  }

  public Logger( boolean debug ) throws
                                 IOException {
    this(debug, "CandleMC");
  }

  public void log( LoggingStrategy strategy, Object... content ) {
    String FORMAT_NORMAL = "[%timestamp] - %log_level - %name - %content";
    String FORMAT_DEBUG = "[%timestamp] - %log_level - %name - %package:%line - %content";
    strategy.log(
            this.name,
            debug ? FORMAT_DEBUG : FORMAT_NORMAL,
            System.currentTimeMillis(),
            content);
  }

  public void debug( Object... content ) {
    log(debugStrategy, content);
  }

  public void error( Object... content ) {
    log(errorStrategy, content);
  }

  public void fatal( Object... content ) {
    log(fatalStrategy, content);
  }

  public void info( Object... content ) {
    log(infoStrategy, content);
  }

  public void stacktrace( Throwable stackTrace ) {
    log(stackTraceStrategy, stackTrace);
  }

  public void warn( Object... content ) {
    log(warnStrategy, content);
  }
}
