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
        gradlePluginPortal()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.androidGradlePluginVersion}")
    }
}

allprojects {
    repositories {
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

// Create base project for creating model outside of this directory
tasks.register("createModelBase") {
    delete("relativitization-model-base")

    doLast {
        val baseDir = "${projectDir.path}/relativitization-model-base"

        File(baseDir).mkdir()

        val gitignoreFile = File(".gitignore")
        val targetGitIgnoreFile = File("$baseDir/.gitignore")
        targetGitIgnoreFile.writeText(gitignoreFile.readText())

        val allFilePathList: List<String> = File(".").walkTopDown().map {
            it.toRelativeString(File("."))
        }.filter {
            it.matches(Regex("^(gradle.*|.*kts?)$"))
        }.toList()

        allFilePathList.forEach {
            targetGitIgnoreFile.appendText(it + "\n")
        }

        listOf(
            "buildSrc",
            "universe",
            "simulations"
        ).forEach { dir ->
            File(dir).walkTopDown().filter {
                it.name.matches(Regex("^.*kts?$"))
            }.forEach {
                // Use the full path such that it works on Windows
                val fromFile = File("${projectDir.path}/${it.path}")
                val toFile = File("$baseDir/${it.path}")
                toFile.parentFile.mkdirs()
                Files.copy(
                    fromFile.toPath(),
                    toFile.toPath(),
                    StandardCopyOption.COPY_ATTRIBUTES,
                    StandardCopyOption.REPLACE_EXISTING,
                )
            }
        }

        File(".").list()!!.filter {
            it.matches(Regex("^(gradle.*|.*kts)$"))
        }.forEach { dir ->
            File(dir).walkTopDown().filter {
                it.isFile
            }.forEach {
                // Use the full path such that it works on Windows
                val fromFile = File("${projectDir.path}/${it.path}")
                val toFile = File("$baseDir/${it.path}")
                toFile.parentFile.mkdirs()
                Files.copy(
                    fromFile.toPath(),
                    toFile.toPath(),
                    StandardCopyOption.COPY_ATTRIBUTES,
                    StandardCopyOption.REPLACE_EXISTING,
                )
            }
        }
    }
}

tasks.register("cleanArt") {
    doLast {
        delete("../relativitization-art/assets/GdxSettings.json")
        delete("../relativitization-art/assets/GenerateSettings.json")
        delete("../relativitization-art/assets/Relativitization.jar")
        delete("../relativitization-art/assets/saves")
        delete("../relativitization-art/outputs")
        delete("../relativitization-art/relativitization-jar")
        delete("../relativitization-art/relativitization-linux")
        delete("../relativitization-art/relativitization-win")
        delete("../relativitization-art/windows-jdk")
    }
}

tasks.register("cleanArtGit") {
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

tasks.register("cleanAll") {
    dependsOn("clean")
    dependsOn("cleanArt")
//    dependsOn("cleanArtGit")
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
    dependsOn("createOutputDir")

    doLast {
        File(
            "${artDirectory.path}/outputs/version.txt"
        ).writeText(Versions.appVersionName)
    }
}

tasks.register("packageAssets") {
    dependsOn("createOutputDir")

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
    dependsOn(":gdx-desktop:fatJar")
    dependsOn("createOutputDir")

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
    dependsOn(":gdx-desktop:fatJar")
    dependsOn("createOutputDir")

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
