package candle.logger;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class StackTraceLoggingStrategy extends CandleLoggingStrategy {
  public StackTraceLoggingStrategy( ExecutorService executor ) {
    super(
            "error",
            "STACKTRACE",
            executor,
            ANSIColors.BOLD_RED
         );
  }

  @Override
  public String generateMessage( String name, StringBuilder builder, String format, long timestamp,
                                 StackTraceElement caller, Object... content ) {
    // Build the message with plain headers and process exceptions
    StringBuilder messageBuilder = new StringBuilder();

    // Header
    String header = String.format("[%s] ----------- STACKTRACE BEGIN -----------", formatTimestamp(timestamp));
    messageBuilder.append(header).append("\n");

    // Process Throwable in content
    for ( Object obj : content ) {
      if ( obj instanceof Throwable throwable ) {
        processThrowable(name, throwable, messageBuilder, timestamp);
      }
    }

    // Footer
    String footer = String.format("[%s] -----------  STACKTRACE END  -----------", formatTimestamp(timestamp));
    messageBuilder.append(footer);

    return messageBuilder.toString();
  }

  @Override
  public String generateConsoleMessage( String name, StringBuilder builder, String format, long timestamp,
                                        StackTraceElement caller, Object... content ) {
    String message = generateMessage(name, builder, format, timestamp, caller, content);
    List<String> lines = Arrays.asList(message.split("\n"));

    // Colorize "STACKTRACE BEGIN" and "STACKTRACE END" in the first and last lines
    String coloredBegin = ANSIColors.BOLD_RED + "STACKTRACE BEGIN" + ANSIColors.RESET;
    String coloredEnd = ANSIColors.BOLD_RED + "STACKTRACE END" + ANSIColors.RESET;

    lines.set(0, lines.getFirst().replace("STACKTRACE BEGIN", coloredBegin));
    lines.set(lines.size() - 1, lines.getLast().replace("STACKTRACE END", coloredEnd));

    return String.join("\n", lines);
  }

  private void processThrowable( String name, Throwable throwable, StringBuilder builder, long timestamp ) {
    builder.append(String.format("[%s] From (Module/Plugin/CandleMC): %s", formatTimestamp(timestamp), name))
           .append("\n");

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
