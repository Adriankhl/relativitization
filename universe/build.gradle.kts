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
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutineVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:${Versions.kotlinxDateTimeVersion}")
                implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinVersion}")
                implementation("org.apache.logging.log4j:log4j-api:${Versions.log4jVersion}")
                implementation("org.apache.logging.log4j:log4j-core:${Versions.log4jVersion}")
                implementation("com.squareup.okio:okio:${Versions.okioVersion}")
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
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(11)
}

tasks {
    test {
        useJUnitPlatform()
    }
}
