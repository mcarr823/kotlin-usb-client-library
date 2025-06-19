package dev.mcarr.usb.interfaces

/**
 * Interface from which any port-listing classes should inherit.
 *
 * This interface is intended to be implemented by classes which
 * enumerate serial ports on a given platform and provide a list
 * of ports to the client.
 * */
interface ISerialPortList {

    /**
     * Retrieves a list of serial ports on the target machine.
     *
     * @return List of serial ports
     * */
    fun get(): List<ISerialPortWrapper>

}