package candle.logger

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.ExecutorService

open class CandleLoggingStrategy : LoggingStrategy {
    val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    private val PATH: Path
    private val LOG_LEVEL: String
    private val executor: ExecutorService
    private val ANSIColor: String

    constructor(path: Path, logLevel: String, executor: ExecutorService, ansiColor: String) {
        this.PATH = path.toAbsolutePath()
        this.LOG_LEVEL = logLevel
        this.executor = executor
        this.ANSIColor = ansiColor
    }

    constructor(
        fileName: String, logLevel: String, executor: ExecutorService,
        ansiColor: String
    ) {
        this.PATH = Path.of(System.getProperty("user.dir"), "logs", fileName.lowercase() + ".log")
            .toAbsolutePath()
        this.LOG_LEVEL = logLevel
        this.executor = executor
        this.ANSIColor = ansiColor
    }

    fun formatTimestamp(timestamp: Long): String {
        return TIME_FORMATTER.format(
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault()
            )
        )
    }

    override fun log(name: String, format: String, timestamp: Long, vararg content: Any) {
        val stackTraceElements = Thread.currentThread().stackTrace
        val caller = stackTraceElements[4]

        executor.execute {
            val stringBuilder = StringBuilder()
            for (`object` in content) {
                stringBuilder.append(`object`)
            }
            try {
                println(
                    generateConsoleMessage(
                        name,
                        stringBuilder,
                        format,
                        timestamp,
                        caller,
                        *content
                    )
                )
                writeToFile(
                    generateLogFileMessage(
                        name,
                        stringBuilder,
                        format,
                        timestamp,
                        caller,
                        *content
                    )
                )
            } catch (e: IOException) {
                System.err.println("Log write error: " + e.message)
            }
        }
    }

    open fun generateMessage(
        name: String, builder: StringBuilder, format: String, timestamp: Long,
        caller: StackTraceElement, vararg content: Any?
    ): String {
        val newFormat = format
            .replace("%timestamp", formatTimestamp(timestamp))
            .replace("%name", name)
            .replace("%package", caller.className)
            .replace("%line", caller.lineNumber.toString())

        return newFormat
            .replace("%content", builder.toString())
            .replace(
                "\n ", """
     
     ${newFormat.replace("%content", "")}
     """.trimIndent()
            ).stripIndent()
    }

    open fun generateConsoleMessage(
        name: String, builder: StringBuilder, format: String, timestamp: Long,
        caller: StackTraceElement, vararg content: Any?
    ): String? {
        return generateMessage(name, builder, format, timestamp, caller, *content)
            .replace("%log_level", ANSIColor + LOG_LEVEL + ANSIColors.RESET)
    }

    fun generateLogFileMessage(
        name: String, builder: StringBuilder, format: String, timestamp: Long,
        caller: StackTraceElement, vararg content: Any?
    ): String {
        var fileFormat = format
        if (!fileFormat.contains("%package:%line")) {
            fileFormat = fileFormat.replaceFirst("%log_level".toRegex(), "%log_level - %package:%line")
        }
        return generateMessage(name, builder, fileFormat, timestamp, caller, *content)
            .replace("%log_level", LOG_LEVEL)
    }

    @Throws(IOException::class)
    override fun writeToFile(message: String) {
        Files.newBufferedWriter(
            PATH,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        ).use { writer ->
            writer.write(message)
            writer.newLine()
        }
    }
}