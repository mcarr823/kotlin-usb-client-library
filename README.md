# Kotlin USB Client Library

## What is it?

This library is a KMM (Kotlin Multiplatform Module) which makes it easier to access USB/Serial Ports from Kotlin code.

It wraps around the [jSerialComm library](https://github.com/Fazecast/jSerialComm) on desktop, and either jSerialComm or [USB Serial For Android](https://github.com/mik3y/usb-serial-for-android) on Android. (iOS is not currently supported)

This library provides:
- kotlin data types (eg. ByteArray)
- coroutine support, with suspending functions or deferred results

## Usage

```Kotlin
// Get available devices
val devices = SerialPortList().get()

// Grab the USB device you want
val device = devices.first()

// Open the device (and close automatically)
device.use{

    // Set the baud rate
    it.setBaudRate(115200)

    // Write some data to the port
    it.write(data, writeTimeoutMilliseconds)

    // Read the response
    val response = it.read(responseSize, readTimeoutMilliseconds)

}
```
