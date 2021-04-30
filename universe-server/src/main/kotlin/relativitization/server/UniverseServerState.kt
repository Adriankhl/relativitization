package relativitization.server

class UniverseServerState(var adminPassword: String) {
    // Whether there is already a universe
    var hasUniverse: Boolean = false

    // map from registered player id to password
    val humanIdPasswordMap: MutableMap<Int, String> = mutableMapOf()

    // Available id list
    val availableIdList: MutableList<Int> = mutableListOf()

    // Clear inactive registered player id each turn or not
    var clearInactive: Boolean = true
}