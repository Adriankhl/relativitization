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


                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlin.reflect)
                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.log4j.core)
                implementation(libs.dataframe)
            }
        }

        val test by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jdkVersion.get()))
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = libs.versions.jvmTargetVersion.get()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(libs.versions.jvmTargetVersion.get().toInt())
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
