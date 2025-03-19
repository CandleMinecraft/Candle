package candle.logger

import java.util.*
import java.util.concurrent.ExecutorService

class StackTraceLoggingStrategy(executor: ExecutorService) : CandleLoggingStrategy(
    "error",
    "STACKTRACE",
    executor,
    ANSIColors.BOLD_RED
) {
    override fun generateMessage(
        name: String,
        builder: StringBuilder,
        format: String,
        timestamp: Long,
        caller: StackTraceElement,
        vararg content: Any?
    ): String {
        // Build the message with plain headers and process exceptions
        val messageBuilder = StringBuilder()

        // Header
        val header = String.format("[%s] ----------- STACKTRACE BEGIN -----------", formatTimestamp(timestamp))
        messageBuilder.append(header).append("\n")

        // Process Throwable in content
        for (obj in content) {
            if (obj is Throwable) {
                processThrowable(name, obj, messageBuilder, timestamp)
            }
        }

        // Footer
        val footer = String.format("[%s] -----------  STACKTRACE END  -----------", formatTimestamp(timestamp))
        messageBuilder.append(footer)

        return messageBuilder.toString()
    }

    override fun generateConsoleMessage(
        name: String,
        builder: StringBuilder,
        format: String,
        timestamp: Long,
        caller: StackTraceElement,
        vararg content: Any?
    ): String? {
        val message = generateMessage(name, builder, format, timestamp, caller, *content)
        val lines = Arrays.asList(*message.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())

        // Colorize "STACKTRACE BEGIN" and "STACKTRACE END" in the first and last lines
        val coloredBegin = ANSIColors.BOLD_RED + "STACKTRACE BEGIN" + ANSIColors.RESET
        val coloredEnd = ANSIColors.BOLD_RED + "STACKTRACE END" + ANSIColors.RESET

        lines[0] = lines.first().replace("STACKTRACE BEGIN", coloredBegin)
        lines[lines.size - 1] = lines.last().replace("STACKTRACE END", coloredEnd)

        return java.lang.String.join("\n", lines)
    }

    private fun processThrowable(name: String, throwable: Throwable, builder: StringBuilder, timestamp: Long) {
        builder.append(String.format("[%s] From (Module/Plugin/CandleMC): %s", formatTimestamp(timestamp), name))
            .append("\n")

        // Haupt-Exception
        addExceptionDetails(throwable, builder, timestamp)

        // Ursachen rekursiv verarbeiten
        var cause = throwable.cause
        while (cause != null) {
            addCauseDetails(cause, builder, timestamp)
            cause = cause.cause
        }
    }

    private fun addExceptionDetails(ex: Throwable, builder: StringBuilder, timestamp: Long) {
        val line = String.format(
            "[%s] Exception: %s\n[%s] Message: %s\n[%s] At: %s - %s:%d",
            formatTimestamp(timestamp),
            ex.javaClass.name,
            formatTimestamp(timestamp),
            if (ex.message != null) ex.message else "N/A",
            formatTimestamp(timestamp),
            ex.stackTrace[0].className,
            ex.stackTrace[0].fileName,
            ex.stackTrace[0].lineNumber
        )
        builder.append(line).append("\n")
    }

    private fun addCauseDetails(cause: Throwable, builder: StringBuilder, timestamp: Long) {
        val line = String.format(
            "[%s] Caused by: %s\n[%s] Message: %s",
            formatTimestamp(timestamp),
            cause.javaClass.name,
            formatTimestamp(timestamp),
            if (cause.message != null) cause.message else "N/A"
        )
        builder.append(line).append("\n")
    }
}
