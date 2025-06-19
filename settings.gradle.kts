pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()

        // For the android serial library
        // https://github.com/mik3y/usb-serial-for-android
        maven(url = "https://jitpack.io")

    }
}

rootProject.name = "kotlin-usb-client-library"
include(":library")
