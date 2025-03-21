package net.candlemc.io

import java.io.ByteArrayOutputStream
import java.io.OutputStream

/**
 * An OutputStream that buffers data ("stages" it) until it is either committed or discarded.
 */
class StagedOutputStream(private val underlying: OutputStream) : OutputStream() {
    private val buffer = ByteArrayOutputStream()

    // Standard OutputStream write methods write to the internal buffer.
    override fun write(b: Int) {
        buffer.write(b)
    }

    override fun write(b: ByteArray) {
        buffer.write(b)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        buffer.write(b, off, len)
    }

    /**
     * Appends a single byte to the stage.
     * Returns the same instance to allow chaining.
     */
    fun append(b: Int): StagedOutputStream {
        write(b)
        return this
    }

    /**
     * Appends an entire byte array to the stage.
     * Returns the same instance to allow chaining.
     */
    fun append(b: ByteArray): StagedOutputStream {
        write(b)
        return this
    }

    /**
     * Appends a portion of a byte array to the stage.
     * Returns the same instance to allow chaining.
     */
    fun append(b: ByteArray, off: Int, len: Int): StagedOutputStream {
        write(b, off, len)
        return this
    }

    /**
     * Commits the staged data to the underlying output stream.
     * The underlying stream is flushed, and the stage is cleared.
     * Returns the same instance to allow chaining.
     */
    fun commitStage(): StagedOutputStream {
        buffer.writeTo(underlying)
        underlying.flush()
        buffer.reset() // Clear the stage after commit.
        return this
    }

    /**
     * Discards the staged data without writing it to the underlying output stream.
     * Returns the same instance to allow chaining.
     */
    fun discardStage(): StagedOutputStream {
        buffer.reset() // Simply clear the buffer.
        return this
    }

    /**
     * Closes the underlying output stream.
     * Note: It does not automatically commit any pending staged data.
     */
    override fun close() {
        underlying.close()
    }

    companion object {
        /**
         * Convenience factory method for creating a StagedOutputStream.
         */
        fun withUnderlying(underlying: OutputStream): StagedOutputStream {
            return StagedOutputStream(underlying)
        }
    }
}
