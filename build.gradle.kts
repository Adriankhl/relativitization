plugins {
    kotlin("jvm") version Versions.kotlinVersion
    kotlin("plugin.serialization") version Versions.kotlinVersion
    id("org.jetbrains.dokka") version Versions.dokkaVersion
}

val artDirectory = File("../relativitization-art")
val artGitDirectory = File("../relativitization-art/.git")
val winePath = "${System.getProperty("user.home")}/.wine/drive_c/relativitization-output"

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

tasks.register("cleanArt") {
    doLast {
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
    }
}

tasks.register("cleanWine") {
    doLast {
        if (File(winePath).exists()) {
            exec {
                commandLine(
                    "rm",
                    "-r",
                    winePath
                )
            }
        }
    }
}

tasks.register("createOutputDir") {
    doLast {
        exec {
            workingDir = artDirectory
            commandLine(
                "mkdir",
                "outputs",
            )

        }
    }
}

tasks.register("outputVersionTxt") {
    doLast {
        File(
            "${artDirectory.path}/outputs/version.txt"
        ).writeText(Versions.appVersionName)
    }
}

tasks.register("packageAssets") {
    doLast {
        exec {
            workingDir = artDirectory
            commandLine(
                "zip",
                "-r",
                "./outputs/assets.zip",
                "./assets",
            )
        }
    }
}

tasks.register("packageAll") {
    dependsOn("cleanArt")
    dependsOn("cleanWine")
    dependsOn("createOutputDir")
    dependsOn("outputVersionTxt")
    dependsOn("packageAssets")
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
                "-XX:MaxRAMPercentage=50",
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
                "-XX:MaxRAMPercentage=50",
            )
        }

        // Copy the windows package directory here
        exec {
            workingDir = artDirectory
            commandLine(
                "cp",
                "-r",
                "$winePath/relativitization-win",
                "."
            )
        }

        // Copy the windows package directory here
        exec {
            workingDir = artDirectory
            commandLine(
                "cp",
                "../relativitization/gdx-android/build/outputs/apk/free/debug/relativitization-free-debug.apk",
                "./outputs/relativitization.apk"
            )
        }

        // copy assets with jar
        exec {
            workingDir = artDirectory
            commandLine(
                "cp",
                "-r",
                "./assets",
                "relativitization-jar",
            )
        }

        // zip the assets with jar
        exec {
            workingDir = artDirectory
            commandLine(
                "zip",
                "-r",
                "./outputs/relativitization-jar.zip",
                "./relativitization-jar",
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
