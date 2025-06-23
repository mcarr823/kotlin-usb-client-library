# Kotlin USB Client Library

## What is it?

This library is a KMM (Kotlin Multiplatform Module) which makes it easier to access USB/Serial Ports from Kotlin code.

It wraps around the [jSerialComm library](https://github.com/Fazecast/jSerialComm) on desktop, and either jSerialComm or [USB Serial For Android](https://github.com/mik3y/usb-serial-for-android) on Android. (iOS is not currently supported)

This library provides:
- kotlin data types (eg. ByteArray)
- coroutine support, with suspending functions or deferred results

## Supported platforms

| Platform | Supported | Artifact                      |
|----------|-----------|-------------------------------|
| Core     |           | dev.mcarr.usb:library         |
| JVM      | &check;   | dev.mcarr.usb:library-jvm     |
| Android  | &check;   | dev.mcarr.usb:library-android |
| Native   | &cross;   |                               |
| iOS      | &cross;   |                               |
| Web      | &cross;   |                               |

* Requires Java 21 or higher


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

## API Documentation

Javadoc can be found [here](https://mcarr823.github.io/kotlin-usb-client-library).

## Setup

The setup instructions below assume that you're building a gradle project, with a TOML file for dependency management and KTS files for gradle scripts.

The instructions should still work for other setups with minor changes.

1. Add jitpack to your repositories (only necessary for Android targets):

```Kotlin
// settings.gradle.kts

dependencyResolutionManagement {
    repositories {
        // For the android serial library
        maven(url = "https://jitpack.io")
    }
}
```

2. Add the library definition and version to your TOML file (if you use one):

```toml
# libs.versions.toml

[versions]
usb = "1.0.0"

[libraries]
usb-library-core = { module = "dev.mcarr.usb:library", version.ref = "usb" }
usb-library-jvm = { module = "dev.mcarr.usb:library-jvm", version.ref = "usb" }
usb-library-android = { module = "dev.mcarr.usb:library-android", version.ref = "usb" }
```

3. Add the dependency to your app's build.gradle.kts file for any platforms you want to support:

```Kotlin
// app (not root) build.gradle.kts

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.usb.library.core)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.usb.library.jvm)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.usb.library.android)
            }
        }
    }
}
```

## TODO

- add Linux native support
- look into web support
- test Android version

- automate unit test CI
- check if timeouts are working properly

