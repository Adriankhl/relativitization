package relativitization.universe.data.components.defaults.diplomacy

import kotlinx.serialization.Serializable

@Serializable
data class WarData(
    val warStateMap: Map<Int, WarStateData> = mapOf(),
) {
    fun getWarStateData(id: Int): WarStateData = warStateMap.getOrDefault(
        id,
        WarStateData()
    )
}

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
 * @property initialSubordinateSet the initial subordinate set at the beginning of war
 * @property proposePeace whether this player desire peace for this war
 * @property startTime what integer time does this war start
 * @property warTargetTopLeaderId the top leader Id of the war target, should be detected by mechanism
 * @property isOffensive whether this player is an attacker of this war
 */
@Serializable
data class WarStateData(
    val initialSubordinateSet: Set<Int> = setOf(),
    val proposePeace: Boolean = false,
    val startTime: Int = 0,
    val warTargetTopLeaderId: Int = -1,
    val isOffensive: Boolean = false,
)

@Serializable
data class MutableWarStateData(
    var initialSubordinateSet: MutableSet<Int> = mutableSetOf(),
    var proposePeace: Boolean = false,
    var startTime: Int = 0,
    var warTargetTopLeaderId: Int = -1,
    var isOffensive: Boolean = false,
)