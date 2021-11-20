package relativitization.universe.data.components.default.modifier

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
) {
    /**
     * Get relation change by modifier
     *
     * @param id relation change between the player of the id and this player
     * @param maxReceiveFuelChange max change from receiving fuel
     */
    fun getRelationChange(
        id: Int,
        maxReceiveFuelChange: Double
    ): Double {
        return if (relationModifierMap.containsKey(id)) {
            relationModifierMap.getValue(id).getOverallRelationChange(maxReceiveFuelChange)
        } else {
            0.0
        }
    }
}

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
    fun updateByProperTime(gamma: Double) {
        relationModifierMap.values.forEach { it.updateByProperTime(gamma) }
    }


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

    /**
     * Get relation change by modifier
     *
     * @param id relation change between the player of the id and this player
     * @param maxReceiveFuelChange max change from receiving fuel
     */
    fun getRelationChange(
        id: Int,
        maxReceiveFuelChange: Double
    ): Double {
        return if (relationModifierMap.containsKey(id)) {
            relationModifierMap.getValue(id).getOverallRelationChange(maxReceiveFuelChange)
        } else {
            0.0
        }
    }

    /**
     * Add receive fuel relation modifier
     *
     * @param id relation change between the player of the id and this player
     * @param relationChange the amount of relation change
     * @param duration the duration of this effect left, in player time, affected by time dilation
     */
    fun addReceiveFuelToRelationModifier(
        id: Int,
        relationChange: Double,
        duration: Double,
    ) {
        relationModifierMap.getOrPut(id) {
            MutableRelationModifier()
        }.receiveFuelList.add(
            MutableSingleRelationModifier(
                change = relationChange,
                durationLeft = duration
        )
        )
    }
}

/**
 * Modifier the relation
 */
@Serializable
data class RelationModifier(
    val receiveFuelList: List<SingleRelationModifier> = listOf(),
) {
    /**
     * Get overall relation change
     *
     * @param maxReceiveFuelChange max change from receiving fuel
     */
    fun getOverallRelationChange(
        maxReceiveFuelChange: Double
    ): Double {
        val totalChange: Double = receiveFuelList.fold(0.0) { acc, mutableSingleRelationModifier ->
            acc + mutableSingleRelationModifier.change
        }

        return when {
            totalChange > maxReceiveFuelChange -> maxReceiveFuelChange
            totalChange < -maxReceiveFuelChange -> - maxReceiveFuelChange
            else -> totalChange
        }
    }
}

@Serializable
data class MutableRelationModifier(
    var receiveFuelList: MutableList<MutableSingleRelationModifier> = mutableListOf(),
) {
    /**
     * Update the time by proper (dilated) time of the player
     */
    fun updateByProperTime(gamma: Double) {
        // Clear modifier when time left is smaller than 0
        receiveFuelList.removeIf {
            it.durationLeft < 0.0
        }

        receiveFuelList.forEach {
            it.durationLeft -= 1.0 / gamma
        }
    }

    /**
     * Get overall relation change
     *
     * @param maxReceiveFuelChange max change from receiving fuel
     */
    fun getOverallRelationChange(
        maxReceiveFuelChange: Double
    ): Double {
        val totalChange: Double = receiveFuelList.fold(0.0) { acc, mutableSingleRelationModifier ->
            acc + mutableSingleRelationModifier.change
        }

        return when {
            totalChange > maxReceiveFuelChange -> maxReceiveFuelChange
            totalChange < -maxReceiveFuelChange -> - maxReceiveFuelChange
            else -> totalChange
        }
    }
}


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