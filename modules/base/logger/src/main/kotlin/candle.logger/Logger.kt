package candle.logger

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Logger @JvmOverloads constructor(// Is the Logger in Debug Mode.
    private val debug: Boolean = false, // Name of the Module, Plugin and so on.
    private val name: String = "CandleMC"
) {
    init {
        Files.createDirectories(Path.of(System.getProperty("user.dir") + "/logs"))
    }

    fun log(strategy: LoggingStrategy, vararg content: Any) {
        val FORMAT_NORMAL = "[%timestamp] - %log_level - %name - %content"
        val FORMAT_DEBUG = "[%timestamp] - %log_level - %name - %package:%line - %content"
        strategy.log(
            this.name,
            if (debug) FORMAT_DEBUG else FORMAT_NORMAL,
            System.currentTimeMillis(),
            *content
        )
    }

    fun debug(vararg content: Any) {
        log(debugStrategy, *content)
    }

    fun error(vararg content: Any) {
        log(errorStrategy, *content)
    }

    fun fatal(vararg content: Any) {
        log(fatalStrategy, *content)
    }

    fun info(vararg content: Any) {
        log(infoStrategy, *content)
    }

    fun stacktrace(stackTrace: Throwable) {
        log(stackTraceStrategy, stackTrace)
    }

    fun warn(vararg content: Any) {
        log(warnStrategy, *content)
    }

    companion object {
        // Single Thread Executor for all logging operations
        private val executor: ExecutorService = Executors.newSingleThreadExecutor()
        private val debugStrategy: LoggingStrategy = CandleLoggingStrategy(
            "debug", "DEBUG", executor, ANSIColors.GREEN
        )
        private val errorStrategy: LoggingStrategy = CandleLoggingStrategy(
            "error", "ERROR", executor, ANSIColors.RED
        )
        private val fatalStrategy: LoggingStrategy = CandleLoggingStrategy(
            "error", "FATAL", executor, ANSIColors.BOLD_RED
        )
        private val infoStrategy: LoggingStrategy = CandleLoggingStrategy(
            "info", "INFO", executor, ANSIColors.CYAN
        )
        private val stackTraceStrategy: LoggingStrategy = StackTraceLoggingStrategy(executor)
        private val warnStrategy: LoggingStrategy = CandleLoggingStrategy(
            "info", "WARN", executor, ANSIColors.YELLOW
        )
    }
}
