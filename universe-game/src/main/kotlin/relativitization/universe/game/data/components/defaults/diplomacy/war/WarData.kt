package relativitization.universe.game.data.components.defaults.diplomacy.war

import kotlinx.serialization.Serializable
import ksergen.annotations.GenerateImmutable

/**
 * Data describing a war where this player has involved
 *
 * @property warCoreData the core data of the war
 * @property opponentLeaderIdList the id of leaders of the opponent that are involved in this war
 * @property hasInitialized whether the data has properly initialized
 * @property initialTotalPopulation the population (including subordinates) when the war begin
 */
@GenerateImmutable
data class MutableWarData(
    var warCoreData: WarCoreData = WarCoreData(),
    val opponentLeaderIdList: MutableList<Int> = mutableListOf(),
    var hasInitialized: Boolean = false,
    var initialTotalPopulation: Double = 0.0,
) {
    fun initializePopulation(totalCurrentPopulation: Double) {
        if (!hasInitialized) {
            initialTotalPopulation = totalCurrentPopulation
        }
    }

}

fun WarData.populationFraction(currentPopulation: Double): Double {
    return if (hasInitialized) {
        if (initialTotalPopulation > 0.0) {
            currentPopulation / initialTotalPopulation
        } else {
            1.0
        }
    } else {
        1.0
    }
}

fun MutableWarData.populationFraction(currentPopulation: Double): Double {
    return if (hasInitialized) {
        if (initialTotalPopulation > 0.0) {
            currentPopulation / initialTotalPopulation
        } else {
            1.0
        }
    } else {
        1.0
    }
}

enum class WarReason(private val value: String) {
    INVASION("Invasion"),
    INDEPENDENCE("Independence"),
    ;

    override fun toString(): String {
        return value
    }
}

/**
 * The core data defining a war
 *
 * @property supportId the id of the player in your side
 * @property opponentId the id of the player in the opponent side
 * @property warReason why this war has started
 * @property startTime the starting time of this war
 * @property isOffensive whether the supported player is the attacker
 * @property isDefensive whether the supported player is the defender, note that a player can be
 *  both attacker and defender in rare situation
 */
@Serializable
data class WarCoreData(
    val supportId: Int = -1,
    val opponentId: Int = -1,
    val warReason: WarReason = WarReason.INVASION,
    val startTime: Int = 0,
    val isOffensive: Boolean = false,
    val isDefensive: Boolean = false,
)