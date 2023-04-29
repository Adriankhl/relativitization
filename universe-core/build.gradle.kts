import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    `maven-publish`
    signing
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

val javadocJar = tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

val sourceJar = tasks.register<Jar>("sourceJar") {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.github.adriankhl"
            artifactId = "relativitization-core"
            version = appVersionName(
                libs.versions.appVersionMajor.get(),
                libs.versions.appVersionMinor.get(),
                libs.versions.appVersionPatch.get(),
            )

            from(components["kotlin"])

            artifact(javadocJar.get())
            artifact(sourceJar.get())

            pom {
                name.set("Relativitization-core")
                description.set("A framework for 4D relativistic social simulations")
                url.set("https://github.com/Adriankhl/relativitization")

                licenses {
                    license {
                        name.set("GNU General Public License v3.0 or later")
                        url.set("https://spdx.org/licenses/GPL-3.0-or-later.html")
                    }
                }
                developers {
                    developer {
                        id.set("adriankhl")
                        name.set("Lai Kwun Hang")
                        email.set("adrian.k.h.lai@outlook.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com:Adriankhl/relativitization.git")
                    developerConnection.set("scm:git:ssh://git@github.com:Adriankhl/relativitization.git")
                    url.set("https://github.com/Adriankhl/relativitization/")
                }
            }
        }
    }

    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

            val ossrhUserName: String? by project
            val ossrhPassword: String? by project
            credentials {
                username = ossrhUserName
                password = ossrhPassword
            }
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
