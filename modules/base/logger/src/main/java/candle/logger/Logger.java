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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * High-performance thread-safe logging utility with daily file rotation and ANSI colors.
 * Features microsecond precision, efficient stack trace analysis, and lock-based synchronization.
 */
public class Logger {

  public static void main( String[] args ) {
    Scanner scanner = new Scanner(System.in);

    Logger logger = new LoggerBuilder()
            .setShowLineNumber(true)
            .setShowTime(true)
            .setShowDate(false)
            .setShowClassName(true)
            .setShowLogLevel(true)
            .build();

    System.out.print("Press a key to showcase Info Log...");
    scanner.nextLine();
    System.out.println("Starting showcase...");
    logger.info("Heyo");
    logger.info(1);
    logger.info(logger);
    System.out.println("Finished showcase!");
    System.out.print("Press a key to showcase Warn Log...");
    scanner.nextLine();
    System.out.println("Starting showcase...");
    logger.warn("Heyo");
    logger.warn(1);
    logger.warn(logger);
    System.out.println("Finished showcase!");
    System.out.print("Press a key to showcase Error Log...");
    scanner.nextLine();
    System.out.println("Starting showcase...");
    logger.error("Heyo");
    logger.error(1);
    logger.error(logger);
    System.out.println("Finished showcase!");
    System.out.print("Press a key to showcase Fatal Log...");
    scanner.nextLine();
    System.out.println("Starting showcase...");
    logger.fatal("Heyo");
    logger.fatal(1);
    logger.fatal(logger);
    System.out.println("Finished showcase!");
    System.out.print("Press a key to showcase Time Tracking...");
    scanner.nextLine();
    System.out.println("Starting showcase...");
    logger.trackTime(Logger::forTimeTracking);
    System.out.println("Finished showcase!");
    System.out.print("Press a key to showcase StackTrace...");
    scanner.nextLine();
    System.out.println("Starting showcase...");
    IOException exception = new IOException("ERROR", new Throwable("NOOOO"));
    logger.stacktrace(exception);
    System.out.println("Finished showcase!");
  }

  private static void forTimeTracking() {
    List<Integer> integerList = new ArrayList<>();
    for (int i = 0; i < 2000000; i++)
      integerList.add(i);
    System.out.println(integerList.size());
  }

  // Immutable configuration
  private static final String LOG_DIRECTORY = "logs";
  private static final int CALLER_STACK_DEPTH = 4; // [0]=getStackTrace [1]=getCallerInfo [2]=log [3]=public method

  // Thread-safe state
  private final ReentrantLock lock = new ReentrantLock();
  private volatile String currentLogDate;
  private final Path logDirectory;

  // Other Loggers
  private final StackTraceLogger stackTraceLogger;

  // Configuration flags
  private boolean showDate = false;
  private boolean showTime = true;
  private boolean showLogLevel = true;
  private boolean showClassName = true;
  private boolean showLineNumber = true;

  public Logger( boolean showDate, boolean showTime, boolean showLogLevel,
                 boolean showClassName, boolean showLineNumber ) {
    this.logDirectory = initializeLogDirectory();
    this.currentLogDate = LoggerUtil.getCurrentDate();
    this.showDate = showDate;
    this.showTime = showTime;
    this.showLogLevel = showLogLevel;
    this.showClassName = showClassName;
    this.showLineNumber = showLineNumber;
    stackTraceLogger = new StackTraceLogger(
            logDirectory,
            showDate,
            showTime,
            showLogLevel,
            showClassName,
            showLineNumber
    );
  }

  // Public API
  public void info(Object content) { log(content, LogLevel.INFO); }
  public void warn(Object content) { log(content, LogLevel.WARN); }
  public void error(Object content) { log(content, LogLevel.ERROR); }
  public void fatal(Object content) { log(content, LogLevel.FATAL); }

  public long trackTime( Runnable function ) {
    long start = System.currentTimeMillis();
    function.run();
    long resultTime = System.currentTimeMillis() - start;
    logTrackTime(resultTime, function.getClass().getName());
    return resultTime;  // Returns time in ms
  }
  public <T> long trackTime( Callable<T> function) {
    long start = System.currentTimeMillis();
    try {
      function.call();
    } catch ( Exception e ) {
      return 0;
    }
    long resultTime = System.currentTimeMillis() - start;
    logTrackTime(resultTime, function.getClass().getName());
    return resultTime;  // Returns time in ms
  }
  private void logTrackTime(long resultTime, String className) {
    log(String.format("It took %sms to execute %s", resultTime, className), LogLevel.TRACK_TIME);
  }

  public void stacktrace(Throwable throwable) {
    stackTraceLogger.logStackTrace(throwable);
  }

  /**
   * Core logging method with thread synchronization and daily file rotation.
   */
  private void log(Object content, LogLevel level) {
    final long timestamp = System.currentTimeMillis();
    final String currentDate = LoggerUtil.getCurrentDate();
    final String formattedTime = LoggerUtil.formatTimestamp(timestamp);

    lock.lock();
    try {
      checkDateChange(currentDate);
      StackTraceElement caller = getCallerInfo();
      String logEntry = buildLogEntry(formattedTime, level.getColoredLevel(), caller, content);

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
                               StackTraceElement caller, Object content) {
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
    return sb.append(content).toString();
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
}