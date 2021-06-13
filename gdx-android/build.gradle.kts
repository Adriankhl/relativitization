import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("com.android.application")
}

android {
    compileSdkVersion(30)

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

    defaultConfig {
        applicationId = "relativitization"
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

    kotlinOptions {
      jvmTarget = "11"
    }

    
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:${Versions.androidGradlePluginVersion}")
}
