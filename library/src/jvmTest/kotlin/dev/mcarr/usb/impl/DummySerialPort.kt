package dev.mcarr.usb.impl

import dev.mcarr.usb.abstracts.AbstractSerialPortWrapper
import kotlinx.coroutines.delay
import java.nio.ByteBuffer

/**
 * Dummy implementation of a serial port.
 *
 * Used for unit testing purposes.
 * */
class DummySerialPort : AbstractSerialPortWrapper() {

    /**
     * Keeps track of whether the port is "open" or not, since
     * there's no actual port to check.
     * */
    var socketIsOpen = false

    /**
     * Delay in milliseconds between each read or write to the
     * socket.
     *
     * This is used to simulate the bitrate or latency of the
     * serial port read/write operations, and for testing
     * timeouts.
     * */
    var emitMillis: Long = 0

    /**
     * Bytes which this serial port will "send" to the client which
     * tries to read from it.
     * */
    var bytesToSend = ByteBuffer.allocate(0)

    /**
     * Bytes which this serial port has received from a client which
     * has written bytes to it.
     * */
    var bytesReceived = ByteBuffer.allocate(0)

    /**
     * Prepares this dummy port for an impending read operation
     * by a client.
     *
     * Fills the dummy port with data for the client to read,
     * and specifies how long the dummy port should wait between
     * each byte before sending another to the client.
     *
     * @param bytesToSend Bytes for the dummy port to send to
     * the next client which reads from it
     * @param emitMillis Delay in milliseconds between each byte
     * being received the next time a client tries to read from
     * this dummy port
     * */
    fun prepareRead(bytesToSend: ByteArray, emitMillis: Long){
        this.bytesToSend = ByteBuffer.wrap(bytesToSend)
        this.emitMillis = emitMillis
    }

    /**
     * Prepares this dummy port for an impending write operation
     * by a client.
     *
     * Tells the dummy port how much data to expect from the client,
     * and specifies how long the dummy port should wait between
     * each byte before receiving another from the client.
     *
     * @param bytesToReceive Number of bytes which the dummy port
     * should expect from the next client which writes to it
     * @param emitMillis Delay in milliseconds between each byte
     * being written the next time a client tries to write to
     * this dummy port
     * */
    fun prepareWrite(bytesToReceive: Int, emitMillis: Long){
        this.bytesReceived = ByteBuffer.allocate(bytesToReceive)
        this.emitMillis = emitMillis
    }

    override suspend fun readBytes(buffer: ByteArray): Int {

        var i = 0

        // Loop over the buffer
        while (i < buffer.size) {

            // Wait the specified number of millis before each iteration
            delay(emitMillis)

            // If there are still bytes left to send to the client, then
            // put a byte in the output buffer and continue the loop
            if (bytesToSend.hasRemaining()) {
                buffer.set(i, bytesToSend.get())
                i++
            }else{

                // If there are no more bytes left, enforce a small delay
                // for every iteration until the timeout is reached.
                // If no delay is specified, wait for 10ms each loop iteration.
                //
                // Once the timeout is reached, the enclosing withTimeout block
                // will throw an exception.
                //
                // This is done to mimic the behavior of a real serial port,
                // which might stop producing data and wait endlessly.
                if (emitMillis == 0L){
                    delay(10)
                }

            }
        }

        // If we manage to read the expected amount of data into the buffer,
        // return the number of bytes read into it
        return i

    }

    override suspend fun writeBytes(buffer: ByteArray): Int {

        var i = 0

        // Loop over the buffer
        while (i < buffer.size) {

            // Wait the specified number of millis before each iteration
            delay(emitMillis)

            // If the port hasn't reached its maximum capacity, write to it
            if (i < bytesReceived.capacity()) {
                bytesReceived.put(buffer[i])
                i++
            }else{

                // If the port has reached its maximum capacity, enforce a small delay
                // for every iteration until the timeout is reached.
                // If no delay is specified, wait for 10ms each loop iteration.
                //
                // Once the timeout is reached, the enclosing withTimeout block
                // will throw an exception.
                //
                // This is done to mimic the behavior of a real serial port,
                // which might stop reading data and wait endlessly.
                if (emitMillis == 0L){
                    delay(10)
                }

            }
        }

        // If we manage to write the expected amount of data to the port,
        // return the number of bytes written
        return i

    }

    override fun setTimeout() {
        // Do nothing.
        // We don't need to override the "default" port timeout behavior,
        // since there isn't one.
    }

    override fun setBaudRate(rate: Int) {
        // Do nothing
        // There is no serial port for which to set the value
    }

    override fun bytesAvailable(): Int {
        // Always return 0, since this dummy port will never
        // have any data
        return 0
    }

    override fun open() {
        socketIsOpen = true
    }

    override fun close() {
        socketIsOpen = false
    }

    override fun isOpen(): Boolean {
        return socketIsOpen
    }

}