import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
    id("org.jetbrains.dokka")
}

val ramPercentage: String = project.properties.getOrDefault(
    "ramPercentage",
    "50"
).toString()

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":universe"))


                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationVersion}")
                implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutineVersion}")

                implementation("org.apache.logging.log4j:log4j-core:${Versions.log4jVersion}")
                implementation("org.jetbrains.kotlinx:dataframe:${Versions.dataframeVersion}")
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

tasks.register("showRamPercentage") {
    doLast {
        println("MaxRAMPercentage: $ramPercentage")
    }
}

tasks.withType<JavaExec>().configureEach {
    dependsOn("showRamPercentage")
}

application {
    mainClass.set(project.properties["mainClass"].toString())
    applicationDefaultJvmArgs = listOf("-XX:MaxRAMPercentage=$ramPercentage")
}
