import java.nio.file.Files
import java.nio.file.StandardCopyOption

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

tasks.getByName("clean").doLast {
    delete("./universe/saves")
    delete("relativitization-model-base")
}

tasks.register("updateModelGitignore") {
    doLast {
        val gitignoreFile = File(".gitignore")
        File("model-gitignore.txt").writeText(gitignoreFile.readText())

        val allFilePathList: List<String> = File(".").walkTopDown().map {
            it.toRelativeString(File("."))
        }.filter {
            it.matches(Regex("^(gradle.*|.*kts?)$"))
        }.toList()

        allFilePathList.forEach {
            File("model-gitignore.txt").appendText(it + "\n")
        }
    }
}

// Create base project for creating model outside of this directory
tasks.register("createModelBase") {
    dependsOn("updateModelGitignore")
    doLast {
        val baseDir = "${projectDir.path}/relativitization-model-base"
        listOf(
            "buildSrc",
            "universe",
            "simulations"
        ).forEach { dir ->
            File(dir).walkTopDown().filter {
                it.name.matches(Regex("^.*kts?$"))
            }.forEach {
                val targetFile = File("$baseDir/${it.path}")
                targetFile.parentFile.mkdirs()
                Files.copy(
                    it.toPath(),
                    targetFile.toPath(),
                    StandardCopyOption.COPY_ATTRIBUTES,
                    StandardCopyOption.REPLACE_EXISTING,
                )
            }
        }
        File(".").list()!!.filter {
            it.matches(Regex("^(gradle.*|.*kts)$"))
        }.forEach { dir ->
            File(dir).walkTopDown().forEach {
                val targetFile = File("$baseDir/${it.path}")
                targetFile.parentFile.mkdirs()
                Files.copy(
                    it.toPath(),
                    targetFile.toPath(),
                    StandardCopyOption.COPY_ATTRIBUTES,
                    StandardCopyOption.REPLACE_EXISTING,
                )
            }
        }
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

tasks.register("downloadWindowsJDK") {
    doLast {
        val windowsJDKDir = File("${artDirectory.path}/windows-jdk")
        if (!windowsJDKDir.exists()) {
            windowsJDKDir.mkdir()

            exec {
                workingDir = windowsJDKDir
                commandLine(
                    "wget",
                    "https://api.adoptium.net/v3/binary/latest/17/ga/windows/x64/jdk/hotspot/normal/eclipse?project=jdk",
                    "-O",
                    "jdk.zip",
                )
            }

            exec {
                workingDir = windowsJDKDir
                commandLine(
                    "unzip",
                    "jdk.zip",
                )
            }
        }

        if (!File("$(windowsJDKDir.path}/jdk").exists()) {
            exec {
                workingDir = windowsJDKDir           
                commandLine(
                    "bash",
                    "-c",
                    "find . -maxdepth 1 -name \"jdk-*\" -exec mv {} jdk \\;",
                )
            }
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

tasks.register("packageLinux") {
    mustRunAfter(":gdx-desktop:fatJar")

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
                "--app-version",
                Versions.appVersionName,
                "--java-options",
                "-XX:MaxRAMPercentage=50",
                "--java-options",
                "-Dlogging=release",
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
    }
}

tasks.register("packageWindows") {
    mustRunAfter(":gdx-desktop:fatJar")

    doLast {
        // cross-building package for windows with wine
        exec {
            workingDir = artDirectory
            commandLine(
                "flatpak",
                "--filesystem=home",
                "run",
                "org.winehq.Wine",
                "./windows-jdk/jdk/bin/jpackage.exe",
                "--input",
                "./assets",
                "--dest",
                "C:/",
                "--name",
                "relativitization-win",
                "--main-jar",
                "Relativitization.jar",
                "--type",
                "app-image",
                "--icon",
                "./assets/images/normal/logo/logo.ico",
                "--app-version",
                Versions.appVersionName,
                "--java-options",
                "-XX:MaxRAMPercentage=50",
                "--java-options",
                "-Dlogging=release",
            )
        }

        // zip the windows package
        exec {
            workingDir = artDirectory
            commandLine(
                "mv",
                "${System.getProperty("user.home")}/.var/app/org.winehq.Wine/data/wine/drive_c/relativitization-win",
                "."
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

tasks.register("packageAll") {
    dependsOn("cleanArt")
    dependsOn("createOutputDir")
    dependsOn("downloadWindowsJDK")
    dependsOn("outputVersionTxt")
    dependsOn("packageAssets")
    dependsOn(":gdx-android:assembleStandalone")
    dependsOn(":gdx-android:bundleRelease")
    dependsOn(":gdx-desktop:fatJar")
    dependsOn("packageLinux")
    dependsOn("packageWindows")

    doLast {
        // Copy android standalone apk
        exec {
            workingDir = artDirectory
            commandLine(
                "cp",
                "../relativitization/gdx-android/build/outputs/apk/free/standalone/relativitization-free-standalone.apk",
                "./outputs/relativitization.apk"
            )
        }

        // copy android release apk
        exec {
            workingDir = artDirectory
            commandLine(
                "cp",
                "../relativitization/gdx-android/build/outputs/bundle/freeRelease/relativitization-free-release.aab",
                "./outputs/relativitization-release.aab"
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

    }
}
