import com.github.benmanes.gradle.versions.reporter.PlainTextReporter
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.ben.manes.versions)
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.application) apply false
}

val artDirectory = File("../relativitization-art")
val artGitDirectory = File("../relativitization-art/.git")

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = libs.versions.jvmTargetVersion.get()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(libs.versions.jvmTargetVersion.get().toInt())
}

tasks.withType<DependencyUpdatesTask> {
    gradleReleaseChannel = "current"

    val showUnresolved: Boolean = project.hasProperty("showUnresolved")

    fun isNonStable(version: String): Boolean {
        return listOf(
            "-alpha",
            "-beta",
            "-dev",
            "-rc",
        ).any {
            version.lowercase().contains(it)
        }
    }

    outputFormatter {
        if (!showUnresolved) {
            unresolved.dependencies.clear()
        }

        // temporary fix for: https://github.com/ben-manes/gradle-versions-plugin/issues/733
        outdated.dependencies.removeAll { isNonStable(it.available.milestone.orEmpty()) }

        val plainTextReporter = PlainTextReporter(
            project,
            revision,
            gradleReleaseChannel
        )
        plainTextReporter.write(System.out, this)
    }

    rejectVersionIf {
        // ignored jacoco: https://github.com/ben-manes/gradle-versions-plugin/issues/534
        // ignore wrong dataframe version: https://mvnrepository.com/artifact/org.jetbrains.kotlinx/dataframe
        (candidate.group == "org.jacoco") || isNonStable(candidate.version) ||
                (candidate.module == "dataframe" && candidate.version == "1727")
    }
}

tasks.getByName("clean").doLast {
    delete("./universe-game/saves")
    delete("./simulations/testData")
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
        }.sorted().toList()

        allFilePathList.forEach {
            targetGitIgnoreFile.appendText(it + "\n")
        }

        listOf(
            "buildSrc",
            "universe-game",
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
        delete("../relativitization-art/assets/relativitization.jar")
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
                    "https://api.adoptium.net/v3/binary/latest/${libs.versions.jdkVersion.get()}/ga/windows/x64/jdk/hotspot/normal/eclipse?project=jdk",
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
        ).writeText(
            appVersionName(
                libs.versions.appVersionMajor.get(),
                libs.versions.appVersionMinor.get(),
                libs.versions.appVersionPatch.get(),
            )
        )
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
                "relativitization.jar",
                "--type",
                "app-image",
                "--icon",
                "./assets/images/normal/logo/logo.png",
                "--app-version",
                appVersionName(
                    libs.versions.appVersionMajor.get(),
                    libs.versions.appVersionMinor.get(),
                    libs.versions.appVersionPatch.get(),
                ),
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
                "relativitization.jar",
                "--type",
                "app-image",
                "--icon",
                "./assets/images/normal/logo/logo.ico",
                "--app-version",
                appVersionName(
                    libs.versions.appVersionMajor.get(),
                    libs.versions.appVersionMinor.get(),
                    libs.versions.appVersionPatch.get(),
                ),
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

