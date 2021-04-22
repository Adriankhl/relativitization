package relativitization.universe.data.diplomacy

import kotlinx.serialization.Serializable

/**
 * @property relation map from id to relation as integer
 */
@Serializable
data class DiplomacyData(
    val relation: Map<Int, Int> = mapOf()
)

@Serializable
data class MutableDiplomacyData(
    var relation: MutableMap<Int, Int> = mutableMapOf()
)