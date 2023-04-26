import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    id("maven-publish")
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlin.reflect)
    implementation(libs.log4j.api)
    implementation(libs.log4j.core)
    implementation(libs.okio)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jdkVersion.get()))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.adriankhl.relativitization"
            artifactId = "relativitization-core"
            version = appVersionName(
                libs.versions.appVersionMajor.get(),
                libs.versions.appVersionMinor.get(),
                libs.versions.appVersionPatch.get(),
            )

            from(components["kotlin"])
        }
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
