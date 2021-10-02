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
                implementation(project(":universe-client"))
                implementation(project(":universe-server"))
                implementation(project(":universe"))


                implementation("com.badlogicgames.gdx:gdx:${Versions.gdxVersion}")
                implementation("com.badlogicgames.gdx:gdx-freetype:${Versions.gdxVersion}")

                implementation("io.ktor:ktor-client-core:${Versions.ktorVersion}")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutineVersion}")
                implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinVersion}")
                implementation("org.apache.logging.log4j:log4j-api:${Versions.log4jVersion}")
                implementation("org.apache.logging.log4j:log4j-core:${Versions.log4jVersion}")
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