package dev.mcarr.usb.interfaces

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred

/**
 * Interface from which any serial port implementations should inherit.
 * */
interface ISerialPortWrapper {

    /**
     * Timeout in milliseconds after which a read operation should abort.
     * */
    var defaultReadTimeout: Long

    /**
     * Timeout in milliseconds after which a write operation should abort.
     * */
    var defaultWriteTimeout: Long

    /**
     * Opens a connection to the serial port and closes it automatically
     * after the callback has completed.
     *
     * This is to mimic the way that Closeable classes work.
     * But in this case, the class inheriting from this interface is
     * a wrapper around the closeable resource, so we can't use Closeable
     * directly.
     *
     * @param callback Callback which is invoked after the serial port
     * has been opened
     *
     * @return Result of the callback operation
     * */
    suspend fun <T> use(callback: suspend (ISerialPortWrapper) -> T): T

    /**
     * Sets the values of the port read and write timeouts.
     *
     * @param readTimeout Maximum time in milliseconds which a read
     * operation should take before aborting
     * @param writeTimeout Maximum time in milliseconds which a write
     * operation should take before aborting
     * */
    fun setDefaultTimeouts(readTimeout: Long, writeTimeout: Long)

    /**
     * Reads the specified number of bytes from the port.
     *
     * @param numBytes Number of bytes to read from the port
     *
     * @throws CancellationException If operation takes longer
     * than the time specified by defaultReadTimeout
     * */
    @Throws(CancellationException::class)
    suspend fun read(numBytes: Int): ByteArray

    /**
     * Reads the specified number of bytes from the port.
     *
     * @param numBytes Number of bytes to read from the port
     * @param timeout Time to wait in milliseconds before aborting
     * the request prematurely
     *
     * @throws CancellationException If operation takes longer
     * than the time specified by `timeout`
     * */
    @Throws(CancellationException::class)
    suspend fun read(numBytes: Int, timeout: Long): ByteArray

    /**
     * Reads the specified number of bytes from the port in an
     * asynchronous manner.
     *
     * @param numBytes Number of bytes to read from the port
     * @param scope Coroutine scope within which to run this operation
     *
     * @return Deferred result containing the bytes which were read
     * from the serial port. May throw a cancellation exception
     * */
    fun read(numBytes: Int, scope: CoroutineScope): Deferred<ByteArray>

    /**
     * Reads the specified number of bytes from the port in an
     * asynchronous manner.
     *
     * @param numBytes Number of bytes to read from the port
     * @param timeout Time to wait in milliseconds before aborting
     * the request prematurely
     * @param scope Coroutine scope within which to run this operation
     *
     * @return Deferred result containing the bytes which were read
     * from the serial port. May throw a cancellation exception
     * */
    fun read(numBytes: Int, timeout: Long, scope: CoroutineScope): Deferred<ByteArray>

    /**
     * Writes the provided bytes to the serial port.
     *
     * @param bytes Bytes to write to the serial port
     *
     * @throws CancellationException If operation takes longer
     * than the time specified by defaultWriteTimeout
     * */
    @Throws(CancellationException::class)
    suspend fun write(bytes: ByteArray): Int

    /**
     * Writes the provided bytes to the serial port.
     *
     * @param bytes Bytes to write to the serial port
     * @param timeout Time to wait in milliseconds before aborting
     * the request prematurely
     *
     * @throws CancellationException If operation takes longer
     * than the time specified by `timeout`
     * */
    @Throws(CancellationException::class)
    suspend fun write(bytes: ByteArray, timeout: Long): Int

    /**
     * Writes the provided bytes to the serial port in an
     * asynchronous manner.
     *
     * @param bytes Bytes to write to the serial port
     * @param scope Coroutine scope within which to run this operation
     *
     * @return Deferred result containing the total bytes which were
     * written to the serial port. May throw a cancellation exception
     * */
    fun write(bytes: ByteArray, scope: CoroutineScope): Deferred<Int>

    /**
     * Writes the provided bytes to the serial port in an
     * asynchronous manner.
     *
     * @param bytes Bytes to write to the serial port
     * @param timeout Time to wait in milliseconds before aborting
     * the request prematurely
     * @param scope Coroutine scope within which to run this operation
     *
     * @return Deferred result containing the total bytes which were
     * written to the serial port. May throw a cancellation exception
     * */
    fun write(bytes: ByteArray, timeout: Long, scope: CoroutineScope): Deferred<Int>

    /**
     * Opens a connection to the serial port.
     *
     * The port should always be closed after you are finished with it.
     * */
    fun open()

    /**
     * Closes a connection to the serial port.
     * */
    fun close()

    /**
     * Checks if there is an open connection to the serial port.
     *
     * @return True if the port is open, otherwise false
     * */
    fun isOpen(): Boolean

    /**
     * Sets the baud rate of the connection to the serial port.
     *
     * Should be called after opening the connection.
     *
     * @param rate Baud rate to set for the connection
     * */
    fun setBaudRate(rate: Int)

    /**
     * Checks how many bytes are available to be read from the
     * serial port.
     *
     * Should only be called while the connection is open.
     *
     * @return Number of bytes available to be read
     * */
    fun bytesAvailable(): Int

}