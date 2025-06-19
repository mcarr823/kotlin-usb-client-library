package dev.mcarr.usb.abstracts

import dev.mcarr.usb.interfaces.ISerialPortWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeout

/**
 * Abstract serial port implementation.
 *
 * This class abstracts away the common logic expected of any
 * serial port implementations.
 * */
abstract class AbstractSerialPortWrapper : ISerialPortWrapper {

    override var defaultReadTimeout: Long = 0
    override var defaultWriteTimeout: Long = 0

    /**
     * Reads data from the serial port until the provided buffer is filled.
     *
     * @param buffer ByteArray to fill with data from the serial port
     *
     * @return Number of bytes read from the serial port
     *
     * @throws TimeoutCancellationException If operation takes longer
     * than the time specified by the readTimeout
     * */
    abstract suspend fun readBytes(buffer: ByteArray): Int

    /**
     * Writes the provided byte data to the serial port.
     *
     * @param buffer ByteArray to write to the serial port
     *
     * @return Number of bytes written to the serial port
     *
     * @throws TimeoutCancellationException If operation takes longer
     * than the time specified by the readTimeout
     * */
    abstract suspend fun writeBytes(buffer: ByteArray): Int

    override fun read(numBytes: Int, scope: CoroutineScope): Deferred<ByteArray> {
        return read(numBytes, defaultReadTimeout, scope)
    }

    override fun read(numBytes: Int, timeout: Long, scope: CoroutineScope): Deferred<ByteArray> {

        // Perform the read operation inside of an async block.
        // Then the calling code can wait for the result in its
        // preferred manner.
        return scope.async {
            read(numBytes, timeout)
        }

    }

    override suspend fun read(numBytes: Int): ByteArray {
        return read(numBytes, defaultReadTimeout)
    }

    override suspend fun read(numBytes: Int, timeout: Long): ByteArray {

        // Allocate a buffer of an appropriate size
        val readBuffer = ByteArray(numBytes)

        // Read data into the buffer
        // Throw a timeout exception if it takes too long
        withTimeout(timeout) {
            readBytes(readBuffer)
        }

        // Return the filled buffer
        return readBuffer

    }

    override fun write(bytes: ByteArray, scope: CoroutineScope): Deferred<Int> {
        return write(bytes, defaultWriteTimeout, scope)
    }

    override fun write(bytes: ByteArray, timeout: Long, scope: CoroutineScope): Deferred<Int> {

        // Perform the write operation inside of an async block.
        // Then the calling code can wait for the result in its
        // preferred manner.
        return scope.async {
            write(bytes, timeout)
        }

    }

    override suspend fun write(bytes: ByteArray): Int {
        return write(bytes, defaultWriteTimeout)
    }

    override suspend fun write(bytes: ByteArray, timeout: Long): Int {

        // Write the data to the serial port.
        // Throw a timeout exception if it takes too long
        return withTimeout(timeout) {
            writeBytes(bytes)
        }

    }

    override fun setDefaultTimeouts(
        readTimeout: Long,
        writeTimeout: Long
    ){
        this.defaultReadTimeout = readTimeout
        this.defaultWriteTimeout = writeTimeout
    }

    /**
     * Sets the timeout behavior of the underlying serial port library.
     *
     * This function should be overridden in a manner such that it puts
     * the serial port in blocking mode with no timeout.
     *
     * This library handles the blocking/timeout aspect itself
     * (with coroutines and suspending functions), so we don't
     * want the underlying library to handle it.
     * */
    abstract fun setTimeout()

    override suspend fun <T> use(callback: suspend (ISerialPortWrapper) -> T): T {

        // Open the serial port
        open()

        // Override the default timeout
        setTimeout()

        // Run the callback
        try {
            return callback(this)
        }finally {
            // Close the serial port
            close()
        }
    }

}