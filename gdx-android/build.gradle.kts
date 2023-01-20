import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.dokka")
}

val natives: Configuration by configurations.creating

val androidKeyDir: String = System.getProperty("user.dir") + "/../android/"

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(Versions.jdkVersion))
    }
}

android {
    namespace = "relativitization.app.android"

    compileSdk = Versions.androidSdkVersion
    ndkVersion = Versions.androidNdkVersion

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
        minSdk = Versions.androidMinSdkVersion
        targetSdk = Versions.androidSdkVersion
        versionCode = Versions.appVersionCode
        versionName = Versions.appVersionName

        base.archivesName.set(Versions.appName)
    }

   signingConfigs {
       if (File(androidKeyDir).exists()) {
           create("release") {
               // You need to specify either an absolute path or include the
               // keystore file in the same directory as the build.gradle file.
               storeFile = File("$androidKeyDir/relativitization-release.jks")
               storePassword =
                   File("$androidKeyDir/relativitization-release-key-password.txt").readText()
                       .trim()
               keyAlias = "relativitization-release-key"
               keyPassword =
                   File("$androidKeyDir/relativitization-release-key-password.txt").readText()
                       .trim()
           }
       }
    }


    flavorDimensions += "version"
    productFlavors {
        create("free") {
            dimension = "version"
            applicationIdSuffix = ".free"
            versionNameSuffix = "-free"
        }
    }

    compileOptions {
        sourceCompatibility(Versions.jvmTargetVersion)
        targetCompatibility(Versions.jvmTargetVersion)
    }

    buildTypes {
        if (File(androidKeyDir).exists()) {
            getByName("release") {
                // Disable proguard, since it breaks reflection
                //proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
                isMinifyEnabled = false
                signingConfig = signingConfigs.getByName("release")
            }
        }

        getByName("debug") {
            // Disable proguard, since it breaks reflection
            //proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            isMinifyEnabled = false
            isDebuggable = true
        }

        // standalone release, without sign
        create("standalone") {
            initWith(getByName("release"))
            signingConfig = getByName("debug").signingConfig
        }
    }

    lint {
        checkReleaseBuilds = false
    }
}

// called every time gradle gets executed, takes the native dependencies of
// the natives' configuration, and extracts them to the proper libs/ folders,
// so they get packed with the APK.
task("copyAndroidNatives") {
    doFirst {
        val rx = Regex(""".*natives-([^.]+)\.jar$""")
        natives.forEach { jar ->
            if (rx.matches(jar.name)) {
                val outputDir = file(rx.replace(jar.name) { "libs/" + it.groups[1]!!.value })
                outputDir.mkdirs()
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
    if ("package" in name || "assemble" in name || "bundleRelease" in name) {
        dependsOn("copyAndroidNatives")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = Versions.jvmTargetVersion.toString()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(Versions.jvmTargetVersion)
}
