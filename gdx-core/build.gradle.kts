import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
}

dependencies {
    implementation(project(":universe-client"))
    implementation(project(":universe-core"))
    implementation(project(":universe-game"))

    implementation(libs.gdx)
    implementation(libs.gdx.freetype)

    implementation(libs.ktor.client.core)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.reflect)
    implementation(libs.log4j.api)
    implementation(libs.log4j.core)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jdkVersion.get()))
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = libs.versions.jvmTargetVersion.get()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(libs.versions.jvmTargetVersion.get().toInt())
}