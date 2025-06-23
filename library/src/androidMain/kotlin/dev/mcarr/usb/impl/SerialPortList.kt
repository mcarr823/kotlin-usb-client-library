package dev.mcarr.usb.impl

import com.fazecast.jSerialComm.SerialPort
import dev.mcarr.usb.abstracts.AbstractSerialPortList
import dev.mcarr.usb.abstracts.AbstractSerialPortWrapper
import dev.mcarr.usb.interfaces.ISerialPortList
import dev.mcarr.usb.interfaces.ISerialPortWrapper

/**
 * Port-listing class which can be used to query the ports
 * on a given machine which have been identified by the
 * jSerialComm library.
 * */
class SerialPortList : AbstractSerialPortList() {

    override fun get(): List<ISerialPortWrapper> {
        return SerialPort.getCommPorts().map { SerialPortWrapper(it) }
    }

}