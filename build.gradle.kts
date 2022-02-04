plugins {
    kotlin("jvm") version Versions.kotlinVersion
    kotlin("plugin.serialization") version Versions.kotlinVersion
    id("org.jetbrains.dokka") version Versions.dokkaVersion
}

val artDirectory = File("../relativitization-art")
val artGitDirectory = File("../relativitization-art/.git")

buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.androidGradlePluginVersion}")

    }
}

allprojects {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
        google()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
    }
}

tasks.register("packageAll") {
    // art directory may not be a git repository
    if (artGitDirectory.exists()) {
        exec {
            workingDir = artDirectory
            commandLine(
                "git",
                "clean",
                "-xfdf",
            )
        }
    }

    // Clean wine output directory
    exec {
        commandLine(
            "rm",
            "-r",
            "${System.getProperty("user.home")}/.wine/drive_c/relativitization-output",
        )
    }

    // create directory to store output
    exec {
         workingDir = artDirectory
         commandLine(
             "mkdir",
             "outputs",
        )
    }

    // zip the assets
    exec {
         workingDir = artDirectory
         commandLine(
             "zip",
             "-r",
             "./outputs/assets.zip",
             "./assets",
        )
    }

    dependsOn(":gdx-android:assembleDebug")
    dependsOn(":gdx-desktop:fatJar")

    doLast {
        // package for linux
        exec {
            workingDir = artDirectory
            commandLine(
                "jpackage",
                "--input",
                "./assets",
                "--name",
                "relativitization-linux",
                "--main-jar",
                "Relativitization.jar",
                "--type",
                "app-image",
                "--icon",
                "./assets/images/normal/logo/logo.png",
                "--java-options",
                "-XX:MaxRAMPercentage=60",
            )
        }

        // cross-building package for windows with wine
        exec {
            workingDir = artDirectory
            commandLine(
                "wine",
                "../windows/jdk/jdk-17/bin/jpackage.exe",
                "--input",
                "./assets",
                "--dest",
                "C:/relativitization-output",
                "--name",
                "relativitization-win",
                "--main-jar",
                "Relativitization.jar",
                "--type",
                "app-image",
                "--icon",
                "./assets/images/normal/logo/logo.ico",
                "--java-options",
                "-XX:MaxRAMPercentage=60",
            )
        }

        // Copy the windows package directory here
        exec {
            workingDir = artDirectory
            commandLine(
                "cp",
                "-r",
                "${System.getProperty("user.home")}/.wine/drive_c/relativitization-output/relativitization-win",
                "."
            )
        }

        // Copy the windows package directory here
        exec {
            workingDir = artDirectory
            commandLine(
                "cp",
                "../relativitization/gdx-android/build/outputs/apk/debug/relativitization-debug.apk",
                "./outputs/relativitization.apk"
            )
        }

        // zip the assets with jar
        exec {
             workingDir = artDirectory
             commandLine(
                 "zip",
                 "-r",
                 "./outputs/relativitization-jar.zip",
                 "./assets",
            )
        }

        // zip the linux package
        exec {
             workingDir = artDirectory
             commandLine(
                 "zip",
                 "-r",
                 "./outputs/relativitization-linux.zip",
                 "./relativitization-linux",
            )
        }

        // zip the windows package
        exec {
             workingDir = artDirectory
             commandLine(
                 "zip",
                 "-r",
                 "./outputs/relativitization-win.zip",
                 "./relativitization-win",
            )
        }
    }
}
