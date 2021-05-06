import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm")
}

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":gdx-core"))
                implementation(project(":universe-server"))
                implementation(project(":universe-client"))
                implementation(project(":universe"))


                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationVersion}")
                implementation("io.ktor:ktor-client-core:${Versions.ktorVersion}")
                implementation("io.ktor:ktor-client-cio:${Versions.ktorVersion}")
                implementation("io.ktor:ktor-server-core:${Versions.ktorVersion}")
                implementation("io.ktor:ktor-server-cio:${Versions.ktorVersion}")

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

        val test by getting {
            dependencies {
                implementation(kotlin("test5"))
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