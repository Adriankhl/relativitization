import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

kotlin {
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation("com.badlogicgames.gdx:gdx:${Versions.gdxVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutineVersion}")
                implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinVersion}")
                implementation("io.ktor:ktor-server-core:${Versions.ktorVersion}")
                implementation("io.ktor:ktor-server-netty:${Versions.ktorVersion}")
                implementation("org.apache.logging.log4j:log4j-api:${Versions.log4jVersion}")
                implementation("org.apache.logging.log4j:log4j-core:${Versions.log4jVersion}")
                implementation("com.github.javafaker:javafaker:${Versions.javafakerVersion}")
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "11"
}
