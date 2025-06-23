package dev.mcarr.usb.impl

import android.content.Context
import android.hardware.usb.UsbManager
import com.hoho.android.usbserial.driver.UsbSerialProber
import dev.mcarr.usb.abstracts.AbstractSerialPortList
import dev.mcarr.usb.interfaces.ISerialPortList
import dev.mcarr.usb.interfaces.ISerialPortWrapper

/**
 * "Usb Serial For Android" port-listing class.
 *
 * @param c Context
 * */
class UsfaPortList(
    val c: Context
) : AbstractSerialPortList() {

    override fun get(): List<ISerialPortWrapper> {
        val manager = c.getSystemService(Context.USB_SERVICE) as UsbManager
        return UsbSerialProber.getDefaultProber()
            .findAllDrivers(manager)
            .map { UsfaPortWrapper(c, it) }
    }

}