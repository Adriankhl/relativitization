import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
    id("org.jetbrains.dokka")
}

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":universe"))


                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationVersion}")
                implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutineVersion}")

                implementation("org.apache.logging.log4j:log4j-core:${Versions.log4jVersion}")
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

tasks.withType<JavaCompile>().configureEach {
    options.release.set(11)
}

tasks {
    test {
        useJUnitPlatform()
    }
}

application {
    mainClass.set(project.properties["mainClass"].toString())
    applicationDefaultJvmArgs = listOf("-XX:MaxRAMPercentage=80")
}
