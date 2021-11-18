package relativitization.universe.data.components.modifier

import kotlinx.serialization.Serializable
import kotlin.math.max

/**
 * Modifier about diplomacy
 *
 * @property peaceTreaty store peace treaty, map from other player id to (universe) time left
 * @property relationModifierMap store relation modifier, map from other player id to relation modifier
 */
@Serializable
data class DiplomacyModifierData(
    val peaceTreaty: Map<Int, Int> = mapOf(),
    val relationModifierMap: Map<Int, RelationModifier> = mapOf(),
)

@Serializable
data class MutableDiplomacyModifierData(
    var peaceTreaty: MutableMap<Int, Int> = mutableMapOf(),
    var relationModifierMap: MutableMap<Int, MutableRelationModifier> = mutableMapOf(),
) {


    /**
     * Update the time by universe time
     */
    fun updateByUniverseTime() {
        val toRemoveTreaty: Set<Int> = peaceTreaty.filter {
            it.value <=0
        }.keys
        toRemoveTreaty.forEach { peaceTreaty.remove(it) }

        val allPeaceTreatyId: Set<Int> = peaceTreaty.keys
        allPeaceTreatyId.forEach {
            val originalTime: Int = peaceTreaty.getValue(it)
            peaceTreaty[it] = originalTime - 1
        }
    }


    /**
     * Update the time by proper (dilated) time of the player
     */
    fun updateByProperTime(gamma: Double) {  }


    /**
     * Check can declare war or not
     */
    fun canDeclareWar(id: Int): Boolean {
        return peaceTreaty.containsKey(id)
    }

    /**
     * Store a peace treaty
     *
     * @param id treaty with player of this id
     * @param length length of this peace treaty
     */
    fun setPeaceTreatyWithLength(id: Int, length: Int) {
        if (peaceTreaty.containsKey(id)) {
            val originalLength: Int = peaceTreaty.getValue(id)
            peaceTreaty[id] = max(originalLength, length)
        } else {
            peaceTreaty[id] = length
        }
    }
}

/**
 * Modifier the relation
 */
@Serializable
data class RelationModifier(
    val receiveFuelList: List<SingleRelationModifier> = listOf(),
)

@Serializable
data class MutableRelationModifier(
    var receiveFuelList: MutableList<MutableSingleRelationModifier> = mutableListOf(),
)


/**
 * Effect from a single improve relation act
 *
 * @property change the amount of relation change
 * @property durationLeft the duration of this effect left, in player time, affected by time dilation
 */
@Serializable
data class SingleRelationModifier(
    val change: Double = 0.0,
    val durationLeft: Double = 0.0,
)

@Serializable
data class MutableSingleRelationModifier(
    var change: Double = 0.0,
    var durationLeft: Double = 0.0,
)