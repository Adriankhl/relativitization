object Versions {
    const val appName = "Relativitization"

    // app version
    private const val appVersionMajor = 0
    private const val appVersionMinor = 2
    private const val appVersionPatch = 0
    const val appVersionCode: Int =
        10000 * appVersionMajor + 100 * appVersionMinor + appVersionPatch
    const val appVersionName = "$appVersionMajor.$appVersionMinor.$appVersionPatch"

    // Kotlin multiplatform
    const val kotlinVersion = "1.6.10"
    const val kotlinxCoroutineVersion = "1.6.0"
    const val kotlinxSerializationVersion = "1.3.2"
    const val kotlinxDateTimeVersion = "0.3.2"
    const val ktorVersion = "1.6.7"
    const val okioVersion = "3.0.0"

    // Documentation
    const val dokkaVersion = "1.6.10"

    // JVM
    const val gdxVersion = "1.10.0"
    const val log4jVersion = "2.17.2"
    const val dataframeVersion = "0.8.0-rc-7"

    // Android
    const val androidGradlePluginVersion = "7.3.0-alpha05"
    const val androidAppCompatVersion = "1.4.1"
    const val androidLifeCycleKtxVersion = "2.4.1"
    const val androidFragmentKtxVersion = "1.4.1"

    // Unused in relativitization, for ktor
    const val logbackVersion = "1.2.10"
}
