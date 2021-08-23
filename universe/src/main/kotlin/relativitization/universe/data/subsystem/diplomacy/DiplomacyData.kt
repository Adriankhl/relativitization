package relativitization.universe.data.subsystem.diplomacy

import kotlinx.serialization.Serializable

/**
 * @property relationMap map from other player id to the relation (in integer) between self and
 * that player
 * @property allyList list of ally
 * @property enemyList list of enemy
 */
@Serializable
data class DiplomacyData(
    val relationMap: Map<Int, Int> = mapOf(),
    val allyList: List<Int> = listOf(),
    val enemyList: List<Int> = listOf(),
) {
    fun getRelation(id: Int): Int {
        return relationMap.getOrDefault(id, 0)
    }
}

@Serializable
data class MutableDiplomacyData(
    var relationMap: MutableMap<Int, Int> = mutableMapOf(),
    var allyList: MutableList<Int> = mutableListOf(),
    var enemyList: MutableList<Int> = mutableListOf(),
)