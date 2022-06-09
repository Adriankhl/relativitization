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
    const val kotlinVersion = "1.7.0"
    const val kotlinxCoroutineVersion = "1.6.2"
    const val kotlinxSerializationVersion = "1.3.3"
    const val kotlinxDateTimeVersion = "0.3.3"
    const val ktorVersion = "2.0.2"
    const val okioVersion = "3.1.0"

    // Documentation
    const val dokkaVersion = "1.6.21"

    // JVM
    const val gdxVersion = "1.11.0"
    const val log4jVersion = "2.17.2"
    const val dataframeVersion = "0.8.0-rc-9"

    // Android
    const val androidGradlePluginVersion = "7.2.1"
    const val androidAppCompatVersion = "1.4.2"
    const val androidLifeCycleKtxVersion = "2.4.1"
    const val androidFragmentKtxVersion = "1.4.1"

    // Unused in relativitization, for ktor
    const val logbackVersion = "1.2.11"
}
