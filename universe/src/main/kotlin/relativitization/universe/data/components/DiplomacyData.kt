package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @property relationMap map from other player id to the relation (in integer) between self and
 * that player
 * @property allyList list of ally
 * @property enemyList list of enemy
 */
@Serializable
@SerialName("DiplomacyData")
data class DiplomacyData(
    val relationMap: Map<Int, Int> = mapOf(),
    val allyList: List<Int> = listOf(),
    val enemyList: List<Int> = listOf(),
) : PlayerDataComponent() {
    fun getRelation(id: Int): Int {
        return relationMap.getOrDefault(id, 0)
    }
}

@Serializable
@SerialName("DiplomacyData")
data class MutableDiplomacyData(
    var relationMap: MutableMap<Int, Int> = mutableMapOf(),
    var allyList: MutableList<Int> = mutableListOf(),
    var enemyList: MutableList<Int> = mutableListOf(),
) : MutablePlayerDataComponent()