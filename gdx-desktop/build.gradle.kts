import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm")
    application
    id("org.jetbrains.dokka")
}

val mainClassPath = "relativitization.app.desktop.DesktopLauncherKt"
val assetsFiles = File("../../relativitization-art/assets")

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":gdx-core"))
                implementation(project(":universe-server"))
                implementation(project(":universe-client"))
                implementation(project(":universe"))


                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationVersion}")
                implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutineVersion}")
                implementation("io.ktor:ktor-client-core:${Versions.ktorVersion}")
                implementation("io.ktor:ktor-client-cio:${Versions.ktorVersion}")
                implementation("io.ktor:ktor-server-core:${Versions.ktorVersion}")
                implementation("io.ktor:ktor-server-cio:${Versions.ktorVersion}")

                implementation("org.apache.logging.log4j:log4j-core:${Versions.log4jVersion}")

                implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${Versions.gdxVersion}")
                implementation("com.badlogicgames.gdx:gdx-platform:${Versions.gdxVersion}:natives-desktop")
                implementation("com.badlogicgames.gdx:gdx-freetype-platform:${Versions.gdxVersion}:natives-desktop")

                // This is for the TexturePacker class
                implementation("com.badlogicgames.gdx:gdx-tools:${Versions.gdxVersion}") {
                    exclude(group = "com.badlogicgames.gdx", module = "gdx-backend-lwjgl")
                }
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
        workingDir = assetsFiles
    }
}

application {
    mainClass.set(mainClassPath)
    applicationDefaultJvmArgs = listOf("-XX:MaxRAMPercentage=50")
}

tasks.withType<JavaExec> {
    workingDir = assetsFiles
}

tasks.register<Jar>("fatJar") {
    archiveBaseName.set(Versions.appName)
    destinationDirectory.set(assetsFiles)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Implementation-Version"] = Versions.appVersionName
        attributes["Main-Class"] = mainClassPath
        attributes["Multi-Release"] = "true"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}
