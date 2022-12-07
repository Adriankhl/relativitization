object Versions {
    const val appName = "Relativitization"

    // app version
    private const val appVersionMajor = 0
    private const val appVersionMinor = 2
    private const val appVersionPatch = 3
    private const val appVersionBuild = 0
    const val appVersionCode: Int = 1000000 * appVersionMajor +
            10000 * appVersionMinor +
            100 * appVersionPatch +
            appVersionBuild
    const val appVersionName = "$appVersionMajor.$appVersionMinor.$appVersionPatch"

    // JDK
    const val jdkVersion = 17

    // Kotlin
    const val kotlinVersion = "1.7.22"

    // Gradle plugin
    const val gradleVersionPluginVersion = "0.44.0"

    // Documentation
    const val dokkaVersion = "1.7.20"

    // Kotlin multiplatform
    const val kotlinxCoroutineVersion = "1.6.4"
    const val kotlinxSerializationVersion = "1.4.1"
    const val kotlinxDateTimeVersion = "0.4.0"
    const val ktorVersion = "2.1.3"
    const val okioVersion = "3.2.0"

    // JVM
    const val gdxVersion = "1.11.0"
    const val log4jVersion = "2.19.0"
    const val dataframeVersion = "0.8.1"

    // Android
    const val androidGradlePluginVersion = "7.3.1"
    const val androidAppCompatVersion = "1.5.1"
    const val androidLifeCycleKtxVersion = "2.5.1"
    const val androidFragmentKtxVersion = "1.5.5"
}
