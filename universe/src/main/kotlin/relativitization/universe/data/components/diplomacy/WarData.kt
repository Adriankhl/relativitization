package relativitization.universe.data.components.diplomacy

import kotlinx.serialization.Serializable

@Serializable
data class WarData(
    val warStateMap: Map<Int, WarStateData> = mapOf(),
)

@Serializable
data class MutableWarData(
    val warStateMap: MutableMap<Int, MutableWarStateData> = mutableMapOf(),
)

/**
 * Store the state of a war
 *
 * @property initialSubordinateList the initial subordinate list at the beginning of war
 */
@Serializable
data class WarStateData(
    val initialSubordinateList: List<Int> = listOf()
)

@Serializable
data class MutableWarStateData(
    val initialSubordinateList: MutableList<Int> = mutableListOf()
)