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
            commandLine("git", "clean", "-xfdf")
        }
    }

    dependsOn(":gdx-desktop:fatJar")

    doLast {
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
                "--java-options",
                "-XX:MaxRAMPercentage=60",
            )
        }
    }
}
