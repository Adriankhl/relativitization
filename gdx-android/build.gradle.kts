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
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jdkVersion.get()))
    }
}

android {
    namespace = "relativitization.app.android"

    compileSdk = libs.versions.androidSdkVersion.get().toInt()
    ndkVersion = libs.versions.androidNdkVersion.get()

    buildFeatures {
        aidl = false
        renderScript = false
        shaders = false
        buildConfig = true
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


                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlin.reflect)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.cio)

                implementation(libs.log4j.core)

                implementation(libs.androidx.lifecycle.runtime.ktx)
                implementation(libs.androidx.fragment.ktx)
                implementation(libs.androidx.appcompat)


                implementation(libs.gdx.backend.android)

                natives("com.badlogicgames.gdx:gdx-platform:${libs.versions.gdxVersion.get()}:natives-armeabi-v7a")
                natives("com.badlogicgames.gdx:gdx-platform:${libs.versions.gdxVersion.get()}:natives-arm64-v8a")
                natives("com.badlogicgames.gdx:gdx-platform:${libs.versions.gdxVersion.get()}:natives-x86")
                natives("com.badlogicgames.gdx:gdx-platform:${libs.versions.gdxVersion.get()}:natives-x86_64")
                natives("com.badlogicgames.gdx:gdx-freetype-platform:${libs.versions.gdxVersion.get()}:natives-armeabi-v7a")
                natives("com.badlogicgames.gdx:gdx-freetype-platform:${libs.versions.gdxVersion.get()}:natives-arm64-v8a")
                natives("com.badlogicgames.gdx:gdx-freetype-platform:${libs.versions.gdxVersion.get()}:natives-x86")
                natives("com.badlogicgames.gdx:gdx-freetype-platform:${libs.versions.gdxVersion.get()}:natives-x86_64")
            }
        }
    }

    packaging {
        resources {
            // Excluding unnecessary meta-data:
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("META-INF/DEPENDENCIES.txt")
            excludes.add("META-INF/dependencies.txt")
            excludes.add("META-INF/NOTICE")
            excludes.add("META-INF/LICENSE")
            excludes.add("META-INF/LICENSE.txt")
            excludes.add("META-INF/NOTICE.txt")
            excludes.add("META-INF/robovm/ios/robovm.xml")
        }
    }

    defaultConfig {
        applicationId = "relativitization.app.android"
        minSdk = libs.versions.androidMinSdkVersion.get().toInt()
        targetSdk = libs.versions.androidSdkVersion.get().toInt()
        versionCode =  appVersionCode(
            libs.versions.appVersionMajor.get(),
            libs.versions.appVersionMinor.get(),
            libs.versions.appVersionPatch.get(),
            libs.versions.appVersionBuild.get(),
        )
        versionName =  appVersionName(
            libs.versions.appVersionMajor.get(),
            libs.versions.appVersionMinor.get(),
            libs.versions.appVersionPatch.get(),
        )

        base.archivesName.set(libs.versions.appName.get())
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
        sourceCompatibility(libs.versions.jvmTargetVersion.get())
        targetCompatibility(libs.versions.jvmTargetVersion.get())
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
    kotlinOptions.jvmTarget = libs.versions.jvmTargetVersion.get()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(libs.versions.jvmTargetVersion.get().toInt())
}
