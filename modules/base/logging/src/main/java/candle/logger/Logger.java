package candle.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

/**
 * High-performance thread-safe logging utility with console coloring.
 * Thread-safe: Unknown
 * Features:
 * - Daily log file rotation (filename determined at initialization)
 * - ANSI-colored console output
 * - Microsecond-level precision
 * - Lock-based synchronization
 * - Efficient stack trace analysis
 */
public final class Logger {
  // Immutable formatters and constants
  private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private static final ZoneId ZONE = ZoneId.systemDefault();
  private static final String[] COLORS = {
          "\u001B[34m",  // INFO (blue)
          "\u001B[33m",  // WARN (yellow)
          "\u001B[31m",  // ERROR (red)
          "\u001B[31;1m" // FATAL (bold red)
  };
  private static final String RESET = "\u001B[0m";
  private static final String LOG_DIRECTORY = "logs";

  // Thread-safe components
  private final ReentrantLock lock = new ReentrantLock();
  private final String filePath;

  public Logger() {
    this.filePath = initializeLogDirectory() + File.separator + getCurrentDate() + ".log";
  }

  // Public logging API
  public void info(String message) { log(message, 0); }
  public void warn(String message) { log(message, 1); }
  public void error(String message) { log(message, 2); }
  public void fatal(String message) { log(message, 3); }

  /**
   * Core logging mechanism with thread synchronization.
   * @param message Log message content
   * @param levelIndex Numeric log level (0=INFO, 1=WARN, 2=ERROR, 3=FATAL)
   */
  private void log(String message, int levelIndex) {
    final long timestamp = System.currentTimeMillis();
    final StackTraceElement caller = getCallerInfo();
    final String formattedTime = formatTimestamp(timestamp);
    final String logLevel = resolveLogLevel(levelIndex);

    String logEntry = buildLogEntry(formattedTime, logLevel, caller, message);
    String coloredEntry = colorizeLogEntry(logEntry, levelIndex);

    lock.lock();
    try {
      writeToConsole(coloredEntry);
      appendToLogFile(logEntry);
    } catch (IOException e) {
      handleWriteError(e);
    } finally {
      lock.unlock();
    }
  }

  /**
   * Initializes log directory and returns full path.
   * Shows error message on filesystem failure.
   */
  private String initializeLogDirectory() {
    String dirPath = System.getProperty("user.dir") + File.separator + LOG_DIRECTORY;
    File directory = new File(dirPath);
    if (!directory.exists() && !directory.mkdirs()) {
      System.err.println("Failed to create log directory at: " + dirPath);
    }
    return dirPath;
  }

  /**
   * Gets current date string for file rotation.
   * Uses system timezone and predefined date format.
   */
  private String getCurrentDate() {
    return LocalDateTime.now(ZONE).format(DATE_FMT);
  }

  /**
   * Formats timestamp with milliseconds precision.
   * Converts using system timezone settings.
   */
  private String formatTimestamp(long epochMillis) {
    return TIME_FMT.format(LocalDateTime.ofInstant(
            Instant.ofEpochMilli(epochMillis), ZONE
                                                  ));
  }

  /**
   * Gets calling class information from stack trace.
   * Assumes 3-level call depth (public API method -> log() -> this method).
   */
  private StackTraceElement getCallerInfo() {
    return Thread.currentThread().getStackTrace()[4]; // [0]=getStackTrace, [1]=getCallerInfo, [2]=log, [3]=public API method
  }

  /**
   * Converts numeric level index to human-readable string.
   */
  private String resolveLogLevel(int levelIndex) {
    return switch (levelIndex) {
      case 0 -> "INFO";
      case 1 -> "WARN";
      case 2 -> "ERROR";
      case 3 -> "FATAL";
      default -> throw new IllegalArgumentException("Invalid log level index");
    };
  }

  /**
   * Constructs standardized log entry structure.
   */
  private String buildLogEntry(String time, String level,
                               StackTraceElement caller, String message) {
    return String.format("%s [%s] - %s:%d - %s",
                         time,
                         level,
                         caller.getClassName(),
                         caller.getLineNumber(),
                         message
                        );
  }

  /**
   * Applies ANSI coloring to log level marker.
   */
  private String colorizeLogEntry(String entry, int levelIndex) {
    return entry.replaceFirst("\\[([A-Z]+)]",
                              COLORS[levelIndex] + "[$1]" + RESET);
  }

  /**
   * Writes colored message to system console.
   */
  private void writeToConsole(String coloredMessage) {
    System.out.println(coloredMessage);
  }

  /**
   * Appends raw log entry to current daily file.
   * Creates file if missing, appends otherwise.
   */
  private void appendToLogFile(String entry) throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(
            new File(filePath).toPath(),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND)
    ) {
      writer.write(entry);
      writer.newLine();
    }
  }

  /**
   * Handles file write errors with simple stderr output.
   * More sophisticated handlers could be added here.
   */
  private void handleWriteError(Exception e) {
    System.err.println("Log write failure: " + e.getMessage());
  }
}