import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("com.android.application")
    kotlin("android")
}

val natives by configurations.creating

android {
    compileSdkVersion(30)

    sourceSets {
        getByName("main").apply {
 
            manifest.srcFile("AndroidManifest.xml")
            res.srcDirs("res")
            aidl.srcDirs("src/main/kotlin")
            renderscript.srcDirs("src/main/kotlin")
            java.srcDir("src/main/kotlin")
            assets.srcDirs("../../relativitization-art/assets")
            jniLibs.srcDirs("libs")

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

                implementation("com.badlogicgames.gdx:gdx-backend-android:${Versions.gdxVersion}")

                natives("com.badlogicgames.gdx:gdx-platform:${Versions.gdxVersion}:natives-armeabi-v7a")
                natives("com.badlogicgames.gdx:gdx-platform:${Versions.gdxVersion}:natives-arm64-v8a")
                natives("com.badlogicgames.gdx:gdx-platform:${Versions.gdxVersion}:natives-x86")
                natives("com.badlogicgames.gdx:gdx-platform:${Versions.gdxVersion}:natives-x86_64")
                natives("com.badlogicgames.gdx:gdx-freetype-platform:${Versions.gdxVersion}:natives-armeabi-v7a")
                natives("com.badlogicgames.gdx:gdx-freetype-platform:${Versions.gdxVersion}:natives-arm64-v8a")
                natives("com.badlogicgames.gdx:gdx-freetype-platform:${Versions.gdxVersion}:natives-x86")
                natives("com.badlogicgames.gdx:gdx-freetype-platform:${Versions.gdxVersion}:natives-x86_64")
            }
        }
    }

    packagingOptions {
        // Excluding unnecessary meta-data:
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/DEPENDENCIES.txt")
        exclude("META-INF/dependencies.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/robovm/ios/robovm.xml")
    }

    defaultConfig {
        applicationId = "relativitization.app.android"
        minSdkVersion(30)
        targetSdkVersion(30)
        versionCode = Versions.appCodeNumber
        versionName = Versions.appVersion

        base.archivesBaseName = "relativitization"
    }


    compileOptions {
      sourceCompatibility(JavaVersion.VERSION_11)
      targetCompatibility(JavaVersion.VERSION_11)
    }
 
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}


// called every time gradle gets executed, takes the native dependencies of
// the natives configuration, and extracts them to the proper libs/ folders
// so they get packed with the APK.
task("copyAndroidNatives") {
    val natives: Configuration by configurations

    doFirst {
        file("libs/armeabi/").mkdirs()
        file("libs/armeabi-v7a/").mkdirs()
        file("libs/arm64-v8a/").mkdirs()
        file("libs/x86_64/").mkdirs()
        file("libs/x86/").mkdirs()
        natives.forEach { jar ->
            val outputDir: File? = when {
                jar.name.endsWith("natives-arm64-v8a.jar") -> file("libs/arm64-v8a")
                jar.name.endsWith("natives-armeabi-v7a.jar") -> file("libs/armeabi-v7a")
                jar.name.endsWith("natives-armeabi.jar") -> file("libs/armeabi")
                jar.name.endsWith("natives-x86_64.jar") -> file("libs/x86_64")
                jar.name.endsWith("natives-x86.jar") -> file("libs/x86")
                else -> null
            }
            outputDir?.let {
                copy {
                    from(zipTree(jar))
                    into(outputDir)
                    include("*.so")
                }
            }
        }
    }
}

tasks.whenTaskAdded {
    if ("package" in name) {
        dependsOn("copyAndroidNatives")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
}
