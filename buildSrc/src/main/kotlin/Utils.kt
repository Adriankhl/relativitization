fun appVersionName(
    appVersionMajor: String,
    appVersionMinor: String,
    appVersionPatch: String,
): String {
    return "$appVersionMajor.$appVersionMinor.$appVersionPatch"
}

fun appVersionCode(
    appVersionMajor: String,
    appVersionMinor: String,
    appVersionPatch: String,
    appVersionBuild: String,
): Int {
    return 1000000 * appVersionMajor.toInt() +
            10000 * appVersionMinor.toInt() +
            100 * appVersionPatch.toInt() +
            appVersionBuild.toInt()
}