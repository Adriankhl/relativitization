package relativitization.universe.data.components.modifier

import kotlinx.serialization.Serializable

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

}