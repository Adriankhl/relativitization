import com.android.build.gradle.internal.tasks.StripDebugSymbolsTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.dokka")
}

val natives: Configuration by configurations.creating

android {
    compileSdk = 31

    buildFeatures {
        aidl = false
        renderScript = false
        shaders = false
    }

    sourceSets {
        getByName("main") {
 
            manifest.srcFile("AndroidManifest.xml")
            res.srcDir("res")
            assets.srcDir("../../relativitization-art/assets")
            jniLibs.srcDir("libs")

            // Comment these out, since kotlin paths should be supported by android gradle plugin
            //java.srcDir("src/main/kotlin")

            dependencies {
                implementation(project(":gdx-core"))
                implementation(project(":universe-server"))
                implementation(project(":universe-client"))
                implementation(project(":universe"))


                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationVersion}")
                implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutineVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinxCoroutineVersion}")
                implementation("io.ktor:ktor-client-core:${Versions.ktorVersion}")
                implementation("io.ktor:ktor-client-cio:${Versions.ktorVersion}")
                implementation("io.ktor:ktor-server-core:${Versions.ktorVersion}")
                implementation("io.ktor:ktor-server-cio:${Versions.ktorVersion}")

                implementation("org.apache.logging.log4j:log4j-core:${Versions.log4jVersion}")

                implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidLifeCycleKtxVersion}")
                implementation("androidx.fragment:fragment-ktx:${Versions.androidFragmentKtxVersion}")
                implementation("androidx.appcompat:appcompat:${Versions.androidAppCompatVersion}")


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
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/DEPENDENCIES.txt")
        resources.excludes.add("META-INF/dependencies.txt")
        resources.excludes.add("META-INF/NOTICE")
        resources.excludes.add("META-INF/LICENSE")
        resources.excludes.add("META-INF/LICENSE.txt")
        resources.excludes.add("META-INF/NOTICE.txt")
        resources.excludes.add("META-INF/robovm/ios/robovm.xml")
    }

    defaultConfig {
        applicationId = "relativitization.app.android"
        minSdk = 26
        targetSdk = 31
        versionCode = Versions.appCodeNumber
        versionName = Versions.appVersion

        base.archivesName.set("relativitization")
    }


    compileOptions {
      sourceCompatibility(JavaVersion.VERSION_11)
      targetCompatibility(JavaVersion.VERSION_11)
    }
 
    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

// called every time gradle gets executed, takes the native dependencies of
// the natives' configuration, and extracts them to the proper libs/ folders,
// so they get packed with the APK.
task("copyAndroidNatives") {
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

tasks.withType<JavaCompile>().configureEach {
    options.release.set(11)
}
