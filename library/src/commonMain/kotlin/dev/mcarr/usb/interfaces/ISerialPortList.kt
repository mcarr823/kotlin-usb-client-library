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

    /**
     * Retrieves a list of serial ports on the target machine
     * which match the provided vendor and product ID.
     *
     * @param productId Product ID of the serial port we want to retrieve
     * @param vendorId Vendor ID of the serial port we want to retrieve
     *
     * @return List of serial ports
     * */
    fun get(productId: Int, vendorId: Int): List<ISerialPortWrapper>

    /**
     * Retrieves a list of serial ports on the target machine
     * which match the provided vendor ID.
     *
     * @param vendorId Vendor ID of the serial port we want to retrieve
     *
     * @return List of serial ports
     * */
    fun getByVendor(vendorId: Int): List<ISerialPortWrapper>

    /**
     * Retrieves a list of serial ports on the target machine
     * which match the provided product ID.
     *
     * @param productId Product ID of the serial port we want to retrieve
     *
     * @return List of serial ports
     * */
    fun getByProductId(productId: Int): List<ISerialPortWrapper>

}