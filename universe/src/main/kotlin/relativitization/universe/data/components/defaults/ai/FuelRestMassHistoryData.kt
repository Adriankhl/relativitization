package relativitization.universe.data.components.defaults.ai

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.defaults.physics.FuelRestMassData
import relativitization.universe.data.components.defaults.physics.MutableFuelRestMassData
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.log

/**
 * Store the history of fuel rest mass data
 *
 * @property maxStoredTurn maximum stored fuel rest mass data
 * @property historyList store the history in a list
 */
@Serializable
data class FuelRestMassHistoryData(
    val maxStoredTurn: Int = 5,
    val historyList: List<FuelRestMassData> = listOf(),
)

@Serializable
data class MutableFuelRestMassHistoryData(
    var maxStoredTurn: Int = 5,
    val historyList: MutableList<MutableFuelRestMassData> = mutableListOf(),
) {
    fun addHistory(mutableFuelRestMassData: MutableFuelRestMassData) {
        historyList.add(mutableFuelRestMassData)
        while (historyList.size > maxStoredTurn) {
            historyList.removeFirst()
        }
    }

    /**
     * Whether the production fuel keep in increasing, if history is too short, return true
     *
     * @param turn the number of turn to consider
     */
    fun isProductionFuelStrictlyIncreasing(turn: Int): Boolean {
        return if (historyList.size <= 1) {
            true
        } else {
            val actualTurn: Int = when {
                turn > historyList.size - 1 -> {
                    logger.debug("Reduce turn to history length")
                    historyList.size - 1
                }
                turn <= 0 -> {
                    logger.error("Turn should be larger than 0")
                    1
                }
                else -> {
                    turn
                }
            }
            val subHistory: List<MutableFuelRestMassData> = historyList.takeLast(turn)

            // Check if the production is strictly increasing
            subHistory.mapIndexed { index, mutableFuelRestMassData ->
                if (index >= 1) {
                    mutableFuelRestMassData.production > subHistory[index - 1].production
                } else {
                    true
                }
            }.all { it }
        }
    }

    /**
     * Check whether the latest production fuel is greater than average times a factor
     *
     * @param turn the number of turn to consider
     * @param compareFactor compare the latest
     */
    fun isLastProductionFuelGreaterThanAverage(
        turn: Int,
        compareFactor: Double,
    ): Boolean {
        return if (historyList.size <= 1) {
            true
        } else {
            val actualTurn: Int = when {
                turn > historyList.size - 1 -> {
                    logger.debug("Reduce turn to history length")
                    historyList.size - 1
                }
                turn <= 0 -> {
                    logger.error("Turn should be larger than 0")
                    1
                }
                else -> {
                    turn
                }
            }
            val subHistory: List<MutableFuelRestMassData> = historyList.takeLast(turn)

            val latestProductionFuel: Double = subHistory.last().production

            val averageProductionFuel: Double  = subHistory.take(subHistory.size - 1).sumOf {
                it.production
            } / subHistory.size

            latestProductionFuel > averageProductionFuel * compareFactor
        }
    }

    companion object {
        val logger = RelativitizationLogManager.getLogger()
    }
}