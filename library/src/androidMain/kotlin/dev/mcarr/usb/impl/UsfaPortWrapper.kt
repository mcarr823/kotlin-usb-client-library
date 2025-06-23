package dev.mcarr.usb.impl

import android.content.Context
import android.hardware.usb.UsbManager
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import dev.mcarr.usb.abstracts.AbstractSerialPortWrapper

/**
 * "Usb Serial For Android" port wrapper class.
 *
 * @param c Context
 * @param driver USB driver/device to interact with
 * */
class UsfaPortWrapper(
    c: Context,
    val driver: UsbSerialDriver
) : AbstractSerialPortWrapper() {

    override var vendorId = driver.device.vendorId
    override var productId = driver.device.productId

    val manager = c.getSystemService(Context.USB_SERVICE) as UsbManager
    lateinit var port: UsbSerialPort

    override suspend fun readBytes(buffer: ByteArray): Int {
        val timeout = 0
        return port.read(buffer, timeout)
    }

    override suspend fun writeBytes(buffer: ByteArray): Int {
        val timeout = 0
        port.write(buffer, timeout)
        return buffer.size
    }

    override fun setTimeout() {
        // Unnecessary, since the timeout is set on each
        // individual read and write
    }

    override fun open() {

        val connection = manager.openDevice(driver.device) ?: throw Exception("Failed to open device")

        // Most devices have just one port (port 0), so let's grab the first port
        // TODO At some point this could be made configurable
        port = driver.ports.firstOrNull() ?: throw Exception("Failed to open device")
        port.open(connection)
        //port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)

    }

    override fun close() {
        port.close()
    }

    override fun isOpen(): Boolean {
        return port.isOpen
    }

    override fun setBaudRate(rate: Int) {
        // TODO check what these values actually are
        port.setParameters(rate, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
    }

    override fun bytesAvailable(): Int {
        // TODO find if it has this method
        return 0
    }

}