import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.dokka)
}

group = "dev.mcarr.usb"
version = "1.0.1"

kotlin {
    jvm()
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    //iosX64()
    //iosArm64()
    //iosSimulatorArm64()
    //linuxX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
        val jvmMain by getting {
            dependencies {
                // https://github.com/Fazecast/jSerialComm
                implementation(libs.jserialcomm)
                implementation(libs.kotlinx.coroutines.core.jvm)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.android)
                // https://github.com/Fazecast/jSerialComm
                implementation(libs.jserialcomm)
                // https://github.com/mik3y/usb-serial-for-android
                implementation(libs.usb.serial.android)
            }
        }
    }
}

android {
    namespace = "dev.mcarr.usb.library"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), "library", version.toString())

    pom {
        name = "Kotlin USB Client Library"
        description = "Kotlin Multiplatform Module for accessing Serial Ports from Kotlin code"
        inceptionYear = "2025"
        url = "https://github.com/mcarr823/kotlin-usb-client-library/"
        licenses {
            license {
                name = "GNU GENERAL PUBLIC LICENSE, Version 3"
                url = "https://www.gnu.org/licenses/gpl-3.0.en.html"
                distribution = "https://www.gnu.org/licenses/gpl-3.0.en.html"
            }
        }
        developers {
            developer {
                id = "mcarr823"
                name = "mcarr823"
                url = "https://github.com/mcarr823/"
            }
        }
        scm {
            url = "https://github.com/mcarr823/kotlin-usb-client-library/"
            connection = "scm:git:git://github.com/mcarr823/kotlin-usb-client-library.git"
            developerConnection = "scm:git:ssh://git@github.com/mcarr823/kotlin-usb-client-library.git"
        }
    }
}
