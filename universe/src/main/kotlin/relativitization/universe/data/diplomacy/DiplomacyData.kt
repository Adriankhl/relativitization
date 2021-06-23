package relativitization.universe.data.diplomacy

import kotlinx.serialization.Serializable

/**
 * @property relation map from player id to relation (in integer) between this player and
 * the player of that id
 */
@Serializable
data class DiplomacyData(
    val relation: Map<Int, Int> = mapOf()
)

@Serializable
data class MutableDiplomacyData(
    var relation: MutableMap<Int, Int> = mutableMapOf()
)