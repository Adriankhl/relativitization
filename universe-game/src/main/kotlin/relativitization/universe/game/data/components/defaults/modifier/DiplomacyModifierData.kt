package relativitization.universe.game.data.components.defaults.modifier

import ksergen.annotations.GenerateImmutable
import relativitization.universe.core.data.serializer.DataSerializer
import kotlin.math.max

/**
 * Modifier about diplomacy
 *
 * @property peaceTreaty store peace treaty, map from other player id to (universe) time left
 * @property relationModifierMap store relation modifier, map from other player id to
 *  relation modifier
 */
@GenerateImmutable
data class MutableDiplomacyModifierData(
    var peaceTreaty: MutableMap<Int, Int> = mutableMapOf(),
    var relationModifierMap: MutableMap<Int, MutableRelationModifier> = mutableMapOf(),
) {
    /**
     * Update the time by universe time
     */
    fun updateByUniverseTime() {
        peaceTreaty.values.removeAll {
            it <= 0
        }

        val allPeaceTreatyId: Set<Int> = peaceTreaty.keys
        allPeaceTreatyId.forEach {
            val originalTime: Int = peaceTreaty.getValue(it)
            peaceTreaty[it] = originalTime - 1
        }
    }


    /**
     * Update the time by proper (dilated) time of the player
     */
    fun updateByProperTime() {
        relationModifierMap.values.forEach { it.updateByProperTime() }
    }


    /**
     * Check can declare war or not
     */
    fun hasPeaceTreaty(id: Int): Boolean {
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
     * Add receive fuel relation modifier
     *
     * @param otherPlayerId relation change between the player of the id and this player
     * @param relationChange the amount of relation change
     * @param duration the duration of this effect left, in player time, affected by time dilation
     */
    fun addReceiveFuelToRelationModifier(
        otherPlayerId: Int,
        relationChange: Double,
        duration: Int,
    ) {
        relationModifierMap.getOrPut(otherPlayerId) {
            MutableRelationModifier()
        }.receiveFuelList.add(
            MutableSingleRelationModifier(
                change = relationChange,
                durationLeft = duration
            )
        )
    }
}

fun DiplomacyModifierData.getPeaceTreatyLength(playerId: Int): Int = peaceTreaty.getOrDefault(
    playerId,
    0
)

fun MutableDiplomacyModifierData.getPeaceTreatyLength(playerId: Int): Int = peaceTreaty.getOrDefault(
    playerId,
    0
)

fun DiplomacyModifierData.getRelationModifier(playerId: Int): RelationModifier =
    relationModifierMap.getOrDefault(playerId, DataSerializer.copy(MutableRelationModifier()))

fun MutableDiplomacyModifierData.getRelationModifier(playerId: Int): MutableRelationModifier =
    relationModifierMap.getOrDefault(playerId, MutableRelationModifier())

/**
 * Get relation change by modifier
 *
 * @param id relation change between the player of the id and this player
 * @param maxReceiveFuelChange max change from receiving fuel
 */
fun DiplomacyModifierData.getRelationChange(
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
 * Get relation change by modifier
 *
 * @param otherPlayerId relation change between the player of the id and this player
 * @param maxReceiveFuelChange max change from receiving fuel
 */
fun MutableDiplomacyModifierData.getRelationChange(
    otherPlayerId: Int,
    maxReceiveFuelChange: Double
): Double {
    return if (relationModifierMap.containsKey(otherPlayerId)) {
        relationModifierMap.getValue(otherPlayerId).getOverallRelationChange(
            maxReceiveFuelChange
        )
    } else {
        0.0
    }
}

/**
 * Modifier the relation
 */
@GenerateImmutable
data class MutableRelationModifier(
    var receiveFuelList: MutableList<MutableSingleRelationModifier> = mutableListOf(),
) {
    /**
     * Update the time by proper (dilated) time of the player
     */
    fun updateByProperTime() {
        // Clear modifier when time left is smaller than 0
        receiveFuelList.removeAll {
            it.durationLeft <= 0
        }

        receiveFuelList.forEach {
            it.durationLeft -= 1
        }
    }
}



/**
 * Get overall relation change
 *
 * @param maxReceiveFuelChange max change from receiving fuel
 */
fun RelationModifier.getOverallRelationChange(
    maxReceiveFuelChange: Double
): Double {
    val totalChange: Double = receiveFuelList.fold(0.0) { acc, mutableSingleRelationModifier ->
        acc + mutableSingleRelationModifier.change
    }

    return when {
        totalChange > maxReceiveFuelChange -> maxReceiveFuelChange
        totalChange < -maxReceiveFuelChange -> -maxReceiveFuelChange
        else -> totalChange
    }
}

/**
 * Get overall relation change
 *
 * @param maxReceiveFuelChange max change from receiving fuel
 */
fun MutableRelationModifier.getOverallRelationChange(
    maxReceiveFuelChange: Double
): Double {
    val totalChange: Double = receiveFuelList.fold(
        0.0
    ) { acc, mutableSingleRelationModifier ->
        acc + mutableSingleRelationModifier.change
    }

    return when {
        totalChange > maxReceiveFuelChange -> maxReceiveFuelChange
        totalChange < -maxReceiveFuelChange -> -maxReceiveFuelChange
        else -> totalChange
    }
}

/**
 * Effect from a single improve relation act
 *
 * @property change the amount of relation change
 * @property durationLeft the duration of this effect left, in player time, affected by
 *  time dilation
 */
@GenerateImmutable
data class MutableSingleRelationModifier(
    var change: Double = 0.0,
    var durationLeft: Int = 0,
)