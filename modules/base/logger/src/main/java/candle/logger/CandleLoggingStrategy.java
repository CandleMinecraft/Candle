package candle.logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

public class CandleLoggingStrategy implements LoggingStrategy {
  public final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
  private final Path PATH;
  private final String LOG_LEVEL;
  private final ExecutorService executor;
  private final String ANSIColor;

  public CandleLoggingStrategy( Path path, String logLevel, ExecutorService executor, String ansiColor ) {
    this.PATH = path.toAbsolutePath();
    this.LOG_LEVEL = logLevel;
    this.executor = executor;
    this.ANSIColor = ansiColor;
  }

  public CandleLoggingStrategy( String fileName, String logLevel, ExecutorService executor, String ansiColor ) {
    this.PATH = Path.of(System.getProperty("user.dir"), "logs", fileName.toLowerCase(Locale.ROOT) + ".log")
                    .toAbsolutePath();
    this.LOG_LEVEL = logLevel;
    this.executor = executor;
    this.ANSIColor = ansiColor;
  }

  public String formatTimestamp( long timestamp ) {
    return TIME_FORMATTER.format(
            LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(timestamp),
                    ZoneId.systemDefault()));
  }

  @Override
  public void log( String format, long timestamp, Object... content ) {
    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
    StackTraceElement caller = stackTraceElements[4];

    executor.execute(() -> {
      StringBuilder stringBuilder = new StringBuilder();
      for ( Object object : content ) {
        stringBuilder.append(object);
      }

      try {
        System.out.println(generateConsoleMessage(
                stringBuilder,
                format,
                timestamp,
                caller,
                content));
        writeToFile(generateLogFileMessage(
                stringBuilder,
                format,
                timestamp,
                caller,
                content));
      } catch ( IOException e ) {
        System.err.println("Log write error: " + e.getMessage());
      }
    });
  }

  public String generateConsoleMessage( StringBuilder builder, String format, long timestamp,
                                        StackTraceElement caller, Object... content ) {
    String newFormat = format
            .replace("%timestamp", formatTimestamp(timestamp))
            .replace("%package", caller.getClassName())
            .replace("%line", Integer.toString(caller.getLineNumber()));

    return newFormat
            .replace("%content", builder.toString())
            .replace("\n ", "\n" + newFormat.replace("%content", "")).stripIndent()
            .replace("%log_level", ANSIColor + LOG_LEVEL + ANSIColors.RESET);
  }

  public String generateLogFileMessage( StringBuilder builder, String format, long timestamp,
                                        StackTraceElement caller, Object... content ) {
    String newFormat = format
            .replace("%timestamp", formatTimestamp(timestamp))
            .replace("%package", caller.getClassName())
            .replace("%line", Integer.toString(caller.getLineNumber()));

    return newFormat
            .replace("%content", builder.toString())
            .replace("\n ", "\n" + newFormat.replace("%content", "")).stripIndent()
            .replace("%log_level", LOG_LEVEL);
  }

  @Override
  public void writeToFile( String message ) throws
                                            IOException {
    try ( BufferedWriter writer = Files.newBufferedWriter(PATH,
                                                          StandardCharsets.UTF_8,
                                                          StandardOpenOption.CREATE,
                                                          StandardOpenOption.APPEND) ) {
      writer.write(message);
      writer.newLine();
    }
  }
}