plugins {
    kotlin("jvm") version "${Versions.kotlinVersion}"
    kotlin("plugin.serialization") version "${Versions.kotlinVersion}"
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
