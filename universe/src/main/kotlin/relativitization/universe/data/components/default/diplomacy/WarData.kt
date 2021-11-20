package relativitization.universe.data.components.default.diplomacy

import kotlinx.serialization.Serializable

@Serializable
data class WarData(
    val warStateMap: Map<Int, WarStateData> = mapOf(),
)

@Serializable
data class MutableWarData(
    val warStateMap: MutableMap<Int, MutableWarStateData> = mutableMapOf(),
) {
    fun getWarStateData(id: Int): MutableWarStateData = warStateMap.getOrPut(
        id
    ) {
        MutableWarStateData()
    }
}

/**
 * Store the state of a war
 *
 * @property initialSubordinateList the initial subordinate list at the beginning of war
 * @property proposePeace whether this player desire peace for this war
 */
@Serializable
data class WarStateData(
    val initialSubordinateList: List<Int> = listOf(),
    val proposePeace: Boolean = false,
)

@Serializable
data class MutableWarStateData(
    var initialSubordinateList: MutableList<Int> = mutableListOf(),
    var proposePeace: Boolean = false,
)