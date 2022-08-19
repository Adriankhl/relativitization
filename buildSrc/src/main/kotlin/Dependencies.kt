object Versions {
    const val appName = "Relativitization"

    // app version
    private const val appVersionMajor = 0
    private const val appVersionMinor = 2
    private const val appVersionPatch = 0
    private const val appVersionBuild = 0
    const val appVersionCode: Int = 1000000 * appVersionMajor +
            10000 * appVersionMinor +
            100 * appVersionPatch +
            appVersionBuild
    const val appVersionName = "$appVersionMajor.$appVersionMinor.$appVersionPatch"

    // Kotlin multiplatform
    const val kotlinVersion = "1.7.10"
    const val kotlinxCoroutineVersion = "1.6.4"
    const val kotlinxSerializationVersion = "1.4.0"
    const val kotlinxDateTimeVersion = "0.4.0"
    const val ktorVersion = "2.1.0"
    const val okioVersion = "3.2.0"

    // Documentation
    const val dokkaVersion = "1.7.10"

    // JVM
    const val gdxVersion = "1.11.0"
    const val log4jVersion = "2.17.2"
    const val dataframeVersion = "0.8.0"

    // Android
    const val androidGradlePluginVersion = "7.2.2"
    const val androidAppCompatVersion = "1.5.0"
    const val androidLifeCycleKtxVersion = "2.5.1"
    const val androidFragmentKtxVersion = "1.5.2"
}
