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
import java.util.concurrent.locks.ReentrantLock;

/**
 * High-performance thread-safe logging utility with daily file rotation and ANSI colors.
 * Features microsecond precision, efficient stack trace analysis, and lock-based synchronization.
 */
public class Logger {
  // Immutable configuration
  private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private static final ZoneId ZONE = ZoneId.systemDefault();
  private static final String LOG_DIRECTORY = "logs";
  private static final int CALLER_STACK_DEPTH = 4; // [0]=getStackTrace [1]=getCallerInfo [2]=log [3]=public method

  // Thread-safe state
  private final ReentrantLock lock = new ReentrantLock();
  private volatile String currentLogDate;
  private final Path logDirectory;

  // Configuration flags
  private boolean showDate = false;
  private boolean showTime = true;
  private boolean showLogLevel = true;
  private boolean showClassName = true;
  private boolean showLineNumber = true;

  public Logger( boolean showDate, boolean showTime, boolean showLogLevel,
                 boolean showClassName, boolean showLineNumber ) {
    this.logDirectory = initializeLogDirectory();
    this.currentLogDate = getCurrentDate();
    this.showDate = showDate;
    this.showTime = showTime;
    this.showLogLevel = showLogLevel;
    this.showClassName = showClassName;
    this.showLineNumber = showLineNumber;
  }

  // Public API
  public void info(String message) { log(message, LogLevel.INFO); }
  public void warn(String message) { log(message, LogLevel.WARN); }
  public void error(String message) { log(message, LogLevel.ERROR); }
  public void fatal(String message) { log(message, LogLevel.FATAL); }

  /**
   * Core logging method with thread synchronization and daily file rotation.
   */
  private void log(String message, LogLevel level) {
    final long timestamp = System.currentTimeMillis();
    final String currentDate = getCurrentDate();
    final String formattedTime = formatTimestamp(timestamp);

    lock.lock();
    try {
      checkDateChange(currentDate);
      StackTraceElement caller = getCallerInfo();
      String logEntry = buildLogEntry(formattedTime, level.getColoredLevel(), caller, message);

      writeToConsole(logEntry);
      appendToFile(logEntry.replaceAll("\u001B\\[[0-9;]*[a-zA-Z]", ""), currentDate);
    } catch (IOException e) {
      handleWriteError(e);
    } finally {
      lock.unlock();
    }
  }

  /**
   * Initializes log directory with error handling.
   */
  private Path initializeLogDirectory() {
    Path dir = Path.of(System.getProperty("user.dir"), LOG_DIRECTORY);
    try {
      return Files.createDirectories(dir);
    } catch (IOException e) {
      System.err.println("Failed to create log directory: " + e.getMessage());
      return dir; // Continue with potentially invalid path
    }
  }

  /**
   * Checks if log file needs rotation due to date change.
   */
  private void checkDateChange(String currentDate) {
    if (!currentDate.equals(currentLogDate)) {
      currentLogDate = currentDate;
    }
  }

  /**
   * Gets caller information from pre-determined stack depth.
   */
  private StackTraceElement getCallerInfo() {
    return Thread.currentThread().getStackTrace()[CALLER_STACK_DEPTH];
  }

  /**
   * Constructs log entry with configured elements.
   */
  private String buildLogEntry(String time, String coloredLevel,
                               StackTraceElement caller, String message) {
    StringBuilder sb = new StringBuilder(64);

    // Date/time block
    if (showDate || showTime) {
      sb.append('[');
      if (showDate) sb.append(currentLogDate);
      if (showDate && showTime) sb.append(" ");
      if (showTime) sb.append(time);
      sb.append("] ");
    }

    // Log level
    if (showLogLevel) sb.append(coloredLevel).append(" - ");

    // Class/line information
    if (showClassName || showLineNumber) {
      if (showClassName) sb.append(caller.getClassName());
      if (showLineNumber) {
        if (showClassName) sb.append(':');
        sb.append(caller.getLineNumber());
      }
      sb.append(" - ");
    }

    // Message
    return sb.append(message).toString();
  }

  /**
   * Writes to console with ANSI colors.
   */
  private void writeToConsole(String entry) {
    System.out.println(entry);
  }

  /**
   * Appends to log file with atomic create/append operations.
   */
  private void appendToFile(String entry, String date) throws IOException {
    Path file = logDirectory.resolve(date + ".log");
    try (BufferedWriter writer = Files.newBufferedWriter(file,
                                                         StandardCharsets.UTF_8,
                                                         StandardOpenOption.CREATE,
                                                         StandardOpenOption.APPEND)) {
      writer.write(entry);
      writer.newLine();
    }
  }

  /**
   * Handles I/O errors with simple stderr reporting.
   */
  private void handleWriteError(Exception e) {
    System.err.println("Log write error: " + e.getMessage());
  }

  // Utility methods
  private String getCurrentDate() {
    return LocalDateTime.now(ZONE).format(DATE_FMT);
  }

  private String formatTimestamp(long epochMillis) {
    return TIME_FMT.format(LocalDateTime.ofInstant(
            Instant.ofEpochMilli(epochMillis), ZONE));
  }
}