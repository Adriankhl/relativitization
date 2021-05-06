import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}
dependencies {
    implementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutineVersion}")
                implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinVersion}")
                implementation("org.apache.logging.log4j:log4j-api:${Versions.log4jVersion}")
                implementation("org.apache.logging.log4j:log4j-core:${Versions.log4jVersion}")
                implementation("io.github.serpro69:kotlin-faker:${Versions.kotlinFakerVersion}")
            }
        }
        val test by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
}

tasks {
    test {
        useJUnitPlatform()
    }
}