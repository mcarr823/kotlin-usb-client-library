package dev.mcarr.usb.impl

import com.fazecast.jSerialComm.SerialPort
import dev.mcarr.usb.abstracts.AbstractSerialPortWrapper

/**
 * Serial port implementation based on the jSerialComm library.
 *
 * @param port SerialPort object used for managing a serial port
 * */
class SerialPortWrapper(
    private val port: SerialPort
) : AbstractSerialPortWrapper() {

    override suspend fun readBytes(buffer: ByteArray): Int {
        return port.readBytes(buffer, buffer.size)
    }

    override suspend fun writeBytes(buffer: ByteArray): Int {
        return port.writeBytes(buffer, buffer.size)
    }

    override fun setTimeout(){

        // Set both read and write timeout behavior to BLOCKING
        val timeoutMode = SerialPort.TIMEOUT_READ_BLOCKING or SerialPort.TIMEOUT_WRITE_BLOCKING

        // Set both timeouts to 0.
        // ie. Disable the timeouts (so we can handle them manually instead)
        val readTimeout = 0
        val writeTimeout = 0

        // Write those values to the port library
        port.setComPortTimeouts(timeoutMode, readTimeout, writeTimeout)

    }

    override fun open(){
        port.openPort()
    }

    override fun close() {
        port.closePort()
    }

    override fun isOpen(): Boolean {
        return port.isOpen
    }

    override fun setBaudRate(rate: Int) {
        port.setBaudRate(rate)
    }

    override fun bytesAvailable(): Int {
        return port.bytesAvailable()
    }

}