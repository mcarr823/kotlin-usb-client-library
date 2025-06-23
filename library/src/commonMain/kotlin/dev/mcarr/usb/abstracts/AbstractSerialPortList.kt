package dev.mcarr.usb.abstracts

import dev.mcarr.usb.interfaces.ISerialPortList
import dev.mcarr.usb.interfaces.ISerialPortWrapper

/**
 * Abstract serial port lister implementation.
 *
 * This class abstracts away the common logic associated
 * with filtering port lists, but leaves the actual port
 * listing logic to the classes which implement this
 * abstract class.
 * */
abstract class AbstractSerialPortList : ISerialPortList {

    override fun get(productId: Int, vendorId: Int): List<ISerialPortWrapper> =
        get().filter { it.productId == productId && it.vendorId == vendorId }

    override fun getByProductId(productId: Int): List<ISerialPortWrapper> =
        get().filter { it.productId == productId }

    override fun getByVendor(vendorId: Int): List<ISerialPortWrapper>  =
        get().filter { it.vendorId == vendorId }

}