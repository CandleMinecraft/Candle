package candle.logger

import java.io.IOException

interface LoggingStrategy {
    fun log(name: String, format: String, timestamp: Long, vararg content: Any)

    @Throws(IOException::class)
    fun writeToFile(message: String)
}
