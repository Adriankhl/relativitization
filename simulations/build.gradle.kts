import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
    id("org.jetbrains.dokka")
}

val processorCount: String = project.properties.getOrDefault(
    "processorCount",
    "NA"
).toString()

val ramPercentage: String = project.properties.getOrDefault(
    "ramPercentage",
    "NA"
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

tasks.register("showJVMArgs") {
    doLast {
        val totalNumProcessor: Int = Runtime.getRuntime().availableProcessors()
        println("ActiveProcessorCount: $processorCount/$totalNumProcessor")
        println("MaxRAMPercentage: $ramPercentage")
    }
}

tasks.withType<JavaExec>().configureEach {
    dependsOn("showJVMArgs")
}

application {
    mainClass.set(project.properties["mainClass"].toString())
    applicationDefaultJvmArgs = if (processorCount != "NA") {
        listOf("-XX:ActiveProcessorCount=$processorCount")
    } else {
        listOf()
    } + if (ramPercentage != "NA") {
        listOf("-XX:MaxRAMPercentage=$ramPercentage")
    } else {
        listOf()
    }
}
