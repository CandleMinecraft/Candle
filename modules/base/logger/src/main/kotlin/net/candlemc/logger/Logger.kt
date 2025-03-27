package net.candlemc.logger

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Copyright 2025 © Nelmin
 * I hereby permit anyone to use this Logger when and where they want to, including myself.
 */
object Logger {
    init {
        Files.createDirectories(Path.of(System.getProperty("user.dir") + "/logs"))
    }

    private var debug: Boolean = System.getProperty("debugMode")?.toBoolean() ?: false // Is the Logger in Debug Mode.
    private var name: String = "CandleMC" // Name of the Module, Plugin and so on.

    // Single Thread Executor for all logging operations
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var debugStrategy: DebugLoggingStrategy = DebugLoggingStrategy(executor, debug)
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

    fun setName(name: String) {
        this.name = name
    }

    fun log(strategy: LoggingStrategy, vararg content: Any) {
        val format_normal = "[%timestamp] - %log_level - %name - %content"
        val format_debug = "[%timestamp] - %log_level - %name - %package:%line - %content"
        strategy.log(
            this.name,
            if (debug) format_debug else format_normal,
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
}
