package relativitization.server

import relativitization.universe.Universe
import relativitization.universe.generate.GenerateSetting
import relativitization.universe.generate.GenerateUniverse

class UniverseServerInternal(var adminPassword: String) {
    // Whether there is already a universe
    var hasUniverse: Boolean = false

    // Data of universe
    var universe: Universe = Universe(GenerateUniverse.generate(GenerateSetting()))

    // map from registered player id to password
    val humanIdPasswordMap: MutableMap<Int, String> = mutableMapOf()

    // Available id list
    val availableIdList: MutableList<Int> = mutableListOf()

    // Clear inactive registered player id each turn or not
    var clearInactive: Boolean = true
}