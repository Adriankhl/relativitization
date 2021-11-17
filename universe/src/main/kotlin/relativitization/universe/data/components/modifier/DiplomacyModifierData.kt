package relativitization.universe.data.components.modifier

import kotlinx.serialization.Serializable
import kotlin.math.max

/**
 * Modifier about diplomacy
 *
 * @property peaceTreaty store peace treaty, map from other player id to (universe) time left
 */
@Serializable
data class DiplomacyModifierData(
    val peaceTreaty: Map<Int, Int> = mapOf()
)

@Serializable
data class MutableDiplomacyModifierData(
    var peaceTreaty: MutableMap<Int, Int> = mutableMapOf()
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