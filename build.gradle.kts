plugins {
    kotlin("jvm") version "${com.relativitization.build.BuildConfig.kotlinVersion}"
    kotlin("plugin.serialization") version "${com.relativitization.build.BuildConfig.kotlinVersion}"
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
