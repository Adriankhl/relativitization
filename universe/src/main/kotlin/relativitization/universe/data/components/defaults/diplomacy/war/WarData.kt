package relativitization.universe.data.components.defaults.diplomacy.war

import kotlinx.serialization.Serializable

/**
 * Data describing a war where this player has involved
 *
 * @property warCoreData the core data of the war
 * @property opponentLeaderIdList the leader id list of the opponent of this war, for peace treaty
 * @property hasInitialized whether the data has properly initialized
 * @property initialTotalPopulation the population (including subordinates) when the war begin
 */
@Serializable
data class WarData(
    val warCoreData: WarCoreData = WarCoreData(),
    val opponentLeaderIdList: List<Int> = listOf(),
    private val hasInitialized: Boolean = false,
    private val initialTotalPopulation: Double = 0.0,
) {
    fun populationFraction(currentPopulation: Double): Double {
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
}

@Serializable
data class MutableWarData(
    var warCoreData: WarCoreData = WarCoreData(),
    val opponentLeaderIdList: MutableList<Int> = mutableListOf(),
    private var hasInitialized: Boolean = false,
    private var initialTotalPopulation: Double = 0.0,
) {
    fun initializePopulation(totalCurrentPopulation: Double) {
        if (!hasInitialized) {
            initialTotalPopulation = totalCurrentPopulation
        }
    }

    fun populationFraction(currentPopulation: Double): Double {
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
 * @property isDefensive whether the supported player is the defender, note that a player can be both
 * attacker and defender in rare situation
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