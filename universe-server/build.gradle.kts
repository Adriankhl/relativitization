import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
}

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":universe"))

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutineVersion}")
                implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinVersion}")
                implementation("io.ktor:ktor-server-core:${Versions.ktorVersion}")
                implementation("io.ktor:ktor-server-cio:${Versions.ktorVersion}")
                implementation("io.ktor:ktor-serialization:${Versions.ktorVersion}")
                implementation("org.apache.logging.log4j:log4j-api:${Versions.log4jVersion}")
                implementation("org.apache.logging.log4j:log4j-core:${Versions.log4jVersion}")
                implementation("ch.qos.logback:logback-classic:${Versions.logbackVersion}")
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
}
