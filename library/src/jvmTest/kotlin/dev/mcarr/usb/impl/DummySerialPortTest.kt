package dev.mcarr.usb.impl

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Before
import java.util.concurrent.TimeoutException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Unit tests for the dummy serial port implementation.
 *
 * Mocks the behavior of a real serial port and
 * runs logic tests based on the expected results.
 *
 * This is done because we can't reliably test a "real"
 * USB device inside of an automated unit test.
 *
 * @see DummySerialPort
 * */
class DummySerialPortTest {

    lateinit var port: DummySerialPort

    @Before
    fun setup(){
        port = DummySerialPort()
    }

    /**
     * Test if the open and close functions do what we
     * expect them to.
     * */
    @Test
    fun testOpenAndClose(){

        // Port is closed by default
        assertFalse(port.isOpen())

        // Open the port
        port.open()
        assertTrue(port.isOpen())

        // Close the port
        port.close()
        assertFalse(port.isOpen())

    }

    /**
     * Test if the use function opens and closes
     * the port automatically.
     * */
    @Test
    fun testUse(){

        // Port is closed by default
        assertFalse(port.isOpen())

        // Port is open inside of the use block
        runTest{
            port.use {
                assertTrue(it.isOpen())
            }
        }

        // Port is closed after the use block completes
        assertFalse(port.isOpen())

        runTest{
            // Open the port with a use block, and throw an exception
            // while the port is in use
            try {
                port.use {
                    throw Exception("Intentionally triggered exception")
                }
            }catch (e: Exception){
                //
            }

            // Confirm that the use block has closed the port even though an
            // exception was thrown
            assertFalse(port.isOpen())
        }

    }

    /**
     * Try to read data from a dummy usb device.
     *
     * The dummy device is preloaded with data to be "read"
     * by this test.
     * We then read that data from the device and check if
     * it matches our expectations.
     * */
    @Test
    fun testRead() {

        // Set the read timeout to 100ms
        port.setDefaultTimeouts(100, 0)

        // Empty read
        runTest {
            val bytesRead = port.read(0)
            assertEquals(0, bytesRead.size)
        }

        // Partial read
        runTest {
            val payload = byteArrayOf(0, 1, 2, 3)
            port.prepareRead(payload, 10)
            val bytesRead = port.read(2)
            assertEquals(2, bytesRead.size)
            assertFalse(payload.contentEquals(bytesRead))
        }

        // Full read
        runTest {
            val payload = byteArrayOf(0, 1, 2, 3)
            port.prepareRead(payload, 10)
            val bytesRead = port.read(4)
            assertEquals(4, bytesRead.size)
            assertTrue(payload.contentEquals(bytesRead))
        }

        // Incomplete read
        // Try to read 5 bytes, but only 4 available
        runTest {
            val payload = byteArrayOf(0, 1, 2, 3)
            port.prepareRead(payload, 10)
            var timedOut = false
            try {
                port.read(5)
            } catch (e: TimeoutCancellationException) {
                // Timeout
                timedOut = true
            }
            assertTrue(timedOut)
        }

        // Read timeout
        // Has enough data, but it takes too long to read
        // because of the increased emit time
        runTest {
            val payload = byteArrayOf(0, 1, 2, 3)
            port.prepareRead(payload, 50)
            var timedOut = false
            try {
                port.read(4)
            } catch (e: TimeoutCancellationException) {
                // Timeout
                timedOut = true
            }
            assertTrue(timedOut)
        }

        // Read timeout
        // Has enough data, but it takes too long to read
        // because of the increased data size
        runTest {
            val payload = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            port.prepareRead(payload, 10)
            var timedOut = false
            try {
                port.read(11)
            } catch (e: TimeoutCancellationException) {
                // Timeout
                timedOut = true
            }
            assertTrue(timedOut)
        }

    }

    /**
     * Try to write data to a dummy usb device.
     *
     * The dummy device is initialized with a fake limit and
     * read timeout, so it's possible to feed it too much
     * data, or for the operation to take too long.
     *
     * This is to simulate how a read device might act.
     * ie. It might refuse to read any more data, or it might
     * process the data as it reads and wind up taking too long.
     * */
    @Test
    fun testWrite() {

        port.setDefaultTimeouts(0, 100)

        // Empty write
        runTest {
            val written = port.write(byteArrayOf())
            assertEquals(0, written)
        }

        // Partial write
        // Try to write 5 bytes, but only 4 available
        runTest {
            val payload = byteArrayOf(0, 1, 2, 3)
            port.prepareWrite(5, 10)
            val written = port.write(payload)
            assertEquals(4, written)
            assertFalse(payload.contentEquals(port.bytesReceived.array()))
        }

        // Full write
        runTest {
            val payload = byteArrayOf(0, 1, 2, 3)
            port.prepareWrite(4, 10)
            val written = port.write(payload)
            assertEquals(4, written)
            assertTrue(payload.contentEquals(port.bytesReceived.array()))
        }

        // Incomplete write
        // Device is only willing to accept the first 2 bytes,
        // but we're trying to write 4
        runTest {
            val payload = byteArrayOf(0, 1, 2, 3)
            port.prepareWrite(2, 10)
            var timedOut = false
            try {
                port.write(payload)
            } catch (e: TimeoutCancellationException) {
                // Timeout
                timedOut = true
            }
            assertTrue(timedOut)
        }

        // Write timeout
        // Has enough data, but it takes too long to write
        // because of the increased emit time
        runTest {
            val payload = byteArrayOf(0, 1, 2, 3)
            port.prepareWrite(4, 50)
            var timedOut = false
            try {
                port.write(payload)
            } catch (e: TimeoutCancellationException) {
                // Timeout
                timedOut = true
            }
            assertTrue(timedOut)
        }

        // Write timeout
        // Has enough data, but it takes too long to write
        // because of the increased data size
        runTest {
            val payload = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            port.prepareWrite(11, 10)
            var timedOut = false
            try {
                port.write(payload)
            } catch (e: TimeoutCancellationException) {
                // Timeout
                timedOut = true
            }
            assertTrue(timedOut)
        }

    }

}