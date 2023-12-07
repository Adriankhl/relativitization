plugins {
    `kotlin-dsl-base`
}

repositories {
    gradlePluginPortal()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jdkVersion.get()))
    }
}
