import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm")
}

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":gdxcore"))

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationVersion}")

                implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${Versions.gdxVersion}")
                implementation("com.badlogicgames.gdx:gdx-platform:${Versions.gdxVersion}:natives-desktop")

                // This is for the TexturePacker class
                implementation("com.badlogicgames.gdx:gdx-tools:${Versions.gdxVersion}") {
                    exclude(group = "com.badlogicgames.gdx", module = "gdx-backend-lwjgl")
                }

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutineVersion}")

                implementation("org.apache.logging.log4j:log4j-core:${Versions.log4jVersion}")
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
}