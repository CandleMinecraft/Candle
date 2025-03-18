package candle.logger;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class StackTraceLoggingStrategy extends CandleLoggingStrategy {
  private final String LOG_LEVEL = "STACKTRACE";

  public StackTraceLoggingStrategy( ExecutorService executor ) {
    super(
            "error",
            "STACKTRACE",
            executor,
            ANSIColors.BOLD_RED
         );
  }

  @Override
  public String generateConsoleMessage( StringBuilder builder, String format, long timestamp,
                                        StackTraceElement caller, Object... content ) {
    String finalMessage = generateLogFileMessage(builder, format, timestamp, caller, content);
    // Terminal-Ausgabe: Header anpassen und Footer entfernen
    List<String> lines = Arrays.asList(finalMessage.split("\n"));

    lines.set(0, String.format("[%s] -----------  %sSTACKTRACE BEGIN%s  -----------", formatTimestamp(timestamp),
                               ANSIColors.BOLD_RED, ANSIColors.RESET));
    lines.set(lines.size() - 1,
              String.format("[%s] -----------  %sSTACKTRACE END%s  -----------", formatTimestamp(timestamp),
                            ANSIColors.BOLD_RED, ANSIColors.RESET));
    return String.join("\n", lines);
  }

  @Override
  public String generateLogFileMessage( StringBuilder builder, String format, long timestamp,
                                        StackTraceElement caller, Object... content ) {
    String header = String.format("[%s] ----------- STACKTRACE BEGIN -----------", formatTimestamp(timestamp));
    builder.append(header).append("\n");

    for ( Object obj : content ) {
      if ( obj instanceof Throwable throwable ) {
        processThrowable(throwable, builder, timestamp);
      }
    }

    // Stacktrace-Footer
    String footer = String.format("[%s] -----------  STACKTRACE END  -----------", formatTimestamp(timestamp));
    builder.append(footer);

    return builder.toString();
  }

  private void processThrowable( Throwable throwable, StringBuilder builder, long timestamp ) {
    // Haupt-Exception
    addExceptionDetails(throwable, builder, timestamp);

    // Ursachen rekursiv verarbeiten
    Throwable cause = throwable.getCause();
    while ( cause != null ) {
      addCauseDetails(cause, builder, timestamp);
      cause = cause.getCause();
    }
  }

  private void addExceptionDetails( Throwable ex, StringBuilder builder, long timestamp ) {
    String line = String.format(
            "[%s] Exception: %s\n[%s] Message: %s\n[%s] At: %s - %s:%d",
            formatTimestamp(timestamp),
            ex.getClass().getName(),
            formatTimestamp(timestamp),
            ex.getMessage() != null ? ex.getMessage() : "N/A",
            formatTimestamp(timestamp),
            ex.getStackTrace()[0].getClassName(),
            ex.getStackTrace()[0].getFileName(),
            ex.getStackTrace()[0].getLineNumber()
                               );
    builder.append(line).append("\n");
  }

  private void addCauseDetails( Throwable cause, StringBuilder builder, long timestamp ) {
    String line = String.format(
            "[%s] Caused by: %s\n[%s] Message: %s",
            formatTimestamp(timestamp),
            cause.getClass().getName(),
            formatTimestamp(timestamp),
            cause.getMessage() != null ? cause.getMessage() : "N/A"
                               );
    builder.append(line).append("\n");
  }
}
