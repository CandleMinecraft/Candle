package candle.logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * High-performance thread-safe stack trace logger with precise formatting control.
 * Features per-line headers, ANSI color support, and lock-striped file access.
 */
public class StackTraceLogger {
  // Configuration
  private final boolean showDate;
  private final boolean showTime;
  private final boolean showLogLevel;
  private final boolean showClassName;
  private final boolean showLineNumber;
  private final Path logDirectory;

  // Thread-local resources
  private static final ThreadLocal<StringBuilder> stringBuilders =
          ThreadLocal.withInitial(() -> new StringBuilder(4096));

  // Concurrency control
  private static final ConcurrentHashMap<String, ReentrantLock> fileLocks =
          new ConcurrentHashMap<>();

  // ANSI constants
  private static final char[] ANSI_ESCAPE = {'\u001B', '['};
  private static final String STACKTRACE_HEADER = "Stack trace:";
  private static final String CAUSE_HEADER = "Caused by:";
  private static final String SUPPRESSED_HEADER = "Suppressed:";
  private static final String STACK_PREFIX = "↳ ";
  private static final String COLORED_LEVEL = "\u001B[35;1mSTACKTRACE\u001B[0m";

  public StackTraceLogger(Path logDirectory, boolean showDate, boolean showTime,
                          boolean showLogLevel, boolean showClassName, boolean showLineNumber) {
    this.logDirectory = logDirectory;
    this.showDate = showDate;
    this.showTime = showTime;
    this.showLogLevel = showLogLevel;
    this.showClassName = showClassName;
    this.showLineNumber = showLineNumber;
  }

  public void logStackTrace(Throwable throwable) {
    final long timestamp = System.currentTimeMillis();
    final String currentDate = LoggerUtil.getCurrentDate();
    final String formattedTime = LoggerUtil.formatTimestamp(timestamp);

    StringBuilder sb = stringBuilders.get();
    sb.setLength(0);

    try {
      String baseHeader = buildBaseHeader(formattedTime, currentDate);
      buildStackTrace(sb, throwable, 0, baseHeader);

      String colored = sb.toString();
      String clean = stripAnsi(colored, sb.length());

      ReentrantLock fileLock = fileLocks.computeIfAbsent(
              currentDate, k -> new ReentrantLock());

      fileLock.lock();
      try {
        writeToConsole(colored);
        appendToFile(clean, currentDate);
      } finally {
        fileLock.unlock();
      }
    } catch (Exception e) {
      System.err.println("Stack trace logging failed: " + e.getMessage());
    }
  }

  private void buildStackTrace(StringBuilder sb, Throwable t, int depth, String baseHeader) {
    // Main exception header
    sb.append(baseHeader)
      .append(depth == 0 ? STACKTRACE_HEADER : CAUSE_HEADER)
      .append('\n');

    // Stack trace elements
    for (StackTraceElement element : t.getStackTrace()) {
      sb.append(baseHeader)
        .append(STACK_PREFIX)
        .append(element.getClassName())
        .append('.')
        .append(element.getMethodName())
        .append(" (")
        .append(element.getFileName())
        .append(':')
        .append(element.getLineNumber())
        .append(")\n");
    }

    // Suppressed exceptions
    for (Throwable suppressed : t.getSuppressed()) {
      sb.append(baseHeader).append(SUPPRESSED_HEADER).append('\n');
      buildStackTrace(sb, suppressed, depth + 1, baseHeader);
    }

    // Root cause
    Throwable cause = t.getCause();
    if (cause != null && cause != t) {
      sb.append(baseHeader).append(CAUSE_HEADER).append('\n');
      buildStackTrace(sb, cause, depth + 1, baseHeader);
    }
  }

  private String buildBaseHeader(String time, String date) {
    StringBuilder header = new StringBuilder(128);

    // Date/Time
    if (showDate || showTime) {
      header.append('[');
      if (showDate) header.append(date);
      if (showDate && showTime) header.append(' ');
      if (showTime) header.append(time);
      header.append("] ");
    }

    // Log level
    if (showLogLevel) header.append(COLORED_LEVEL).append(" - ");

    // Class/line info
    if (showClassName || showLineNumber) {
      StackTraceElement caller = getCallerInfo();
      if (showClassName) header.append(caller.getClassName());
      if (showLineNumber) {
        if (showClassName) header.append(':');
        header.append(caller.getLineNumber());
      }
      header.append(" - ");
    }

    return header.toString();
  }

  private StackTraceElement getCallerInfo() {
    StackTraceElement[] stack = Thread.currentThread().getStackTrace();
    for (int i = 4; i < stack.length; i++) {
      String className = stack[i].getClassName();
      if (!className.startsWith("candle.logger")) {
        return stack[i];
      }
    }
    return stack[4];
  }

  private String stripAnsi(String s, int length) {
    StringBuilder sb = new StringBuilder(length);
    boolean inEscape = false;

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);

      if (inEscape) {
        if (c == 'm') inEscape = false;
        continue;
      }

      if (c == ANSI_ESCAPE[0] && i < s.length()-1 && s.charAt(i+1) == ANSI_ESCAPE[1]) {
        inEscape = true;
        i++;
        continue;
      }

      sb.append(c);
    }
    return sb.toString();
  }

  private void writeToConsole(String entry) {
    System.out.print(entry);
  }

  private void appendToFile(String entry, String date) throws IOException {
    Path file = logDirectory.resolve(date + ".log");
    Files.writeString(file, entry,
                      StandardCharsets.UTF_8,
                      StandardOpenOption.CREATE,
                      StandardOpenOption.APPEND);
  }
}