object Versions {
    const val appName = "relativitization"

    // app version
    private const val appVersionMajor = 0
    private const val appVersionMinor = 2
    private const val appVersionPatch = 4
    private const val appVersionBuild = 0
    const val appVersionCode: Int = 1000000 * appVersionMajor +
            10000 * appVersionMinor +
            100 * appVersionPatch +
            appVersionBuild
    const val appVersionName = "$appVersionMajor.$appVersionMinor.$appVersionPatch"

    // JDK
    const val jdkVersion = 17
    const val jvmTargetVersion = 11

    // Android SDK
    const val androidSdkVersion = 33
    const val androidMinSdkVersion = 26
    const val androidNdkVersion = "25.1.8937393"

    // Dependency

    // Kotlin
    const val kotlinVersion = "1.8.10"

    // Gradle plugin
    const val gradleVersionPluginVersion = "0.46.0"

    // Documentation
    const val dokkaVersion = "1.7.20"

    // Kotlin multiplatform
    const val kotlinxCoroutineVersion = "1.6.4"
    const val kotlinxSerializationVersion = "1.5.0"
    const val kotlinxDateTimeVersion = "0.4.0"
    const val ktorVersion = "2.2.4"
    const val okioVersion = "3.3.0"

    // JVM
    const val gdxVersion = "1.11.0"
    const val log4jVersion = "2.20.0"
    const val dataframeVersion = "0.9.1"

    // Android
    const val androidGradlePluginVersion = "7.4.2"
    const val androidAppCompatVersion = "1.6.1"
    const val androidLifeCycleKtxVersion = "2.5.1"
    const val androidFragmentKtxVersion = "1.5.5"
}
