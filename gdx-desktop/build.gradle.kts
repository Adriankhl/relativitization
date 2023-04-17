import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm")
    application
    id("org.jetbrains.dokka")
}

val mainClassPath = "relativitization.app.desktop.DesktopLauncherKt"
val assetsFiles = File("../../relativitization-art/assets")

dependencies {
    implementation(project(":gdx-core"))
    implementation(project(":universe-server"))
    implementation(project(":universe-client"))
    implementation(project(":universe"))


    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)

    implementation(libs.log4j.core)

    implementation(libs.gdx.backend.lwjgl3)

    implementation("com.badlogicgames.gdx:gdx-platform:${libs.versions.gdxVersion.get()}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:${libs.versions.gdxVersion.get()}:natives-desktop")

    // This is for the TexturePacker class
    implementation("com.badlogicgames.gdx:gdx-tools:${libs.versions.gdxVersion.get()}") {
        exclude(group = "com.badlogicgames.gdx", module = "gdx-backend-lwjgl")
    }

    testImplementation(kotlin("test"))
}

kotlin {
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
    mustRunAfter(":packageAssets")
    mustRunAfter(":gdx-android:assembleStandalone")
    mustRunAfter(":gdx-android:bundleRelease")

    archiveBaseName.set(libs.versions.appName)
    destinationDirectory.set(assetsFiles)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Implementation-Version"] = appVersionName(
            libs.versions.appVersionMajor.get(),
            libs.versions.appVersionMinor.get(),
            libs.versions.appVersionPatch.get(),
        )
        attributes["Main-Class"] = mainClassPath
        attributes["Multi-Release"] = "true"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}
