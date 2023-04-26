package relativitization.universe.game.data.components.defaults.ai

import ksergen.annotations.GenerateImmutable
import relativitization.universe.game.data.components.defaults.physics.FuelRestMassData
import relativitization.universe.game.data.components.defaults.physics.MutableFuelRestMassData
import relativitization.universe.core.utils.RelativitizationLogManager

/**
 * Store the history of fuel rest mass data
 *
 * @property maxStoredTurn maximum stored fuel rest mass data
 * @property historyList store the history in a list
 */
@GenerateImmutable
data class MutableFuelRestMassHistoryData(
    var maxStoredTurn: Int = 6,
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
            val subHistory: List<MutableFuelRestMassData> = historyList.takeLast(actualTurn)

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
     * Check whether the production fuel is increasing
     *
     * @param turn the number of turn to consider
     * @param turnCompare the number of recent turn to compare with the history
     * @param compareMultiplier compare the latest average to previous average times this multiplier
     */
    fun isProductionFuelIncreasing(
        turn: Int,
        turnCompare: Int,
        compareMultiplier: Double,
    ): Boolean {
        return if (historyList.size <= 1) {
            true
        } else {
            val actualTurn: Int = when {
                turn > historyList.size -> {
                    logger.debug("Reduce turn to history length")
                    historyList.size
                }
                turn < 2 -> {
                    logger.error("Turn should be larger than 1")
                    2
                }
                else -> {
                    turn
                }
            }

            val actualCompareTurn: Int = when {
                turnCompare > actualTurn - 1 -> {
                    logger.debug("Reduce compareTurn to turn - 1")
                    actualTurn - 1
                }
                turnCompare < 1 -> {
                    logger.error("Turn should be larger than 0")
                    1
                }
                else -> {
                    turnCompare
                }
            }

            val subHistory: List<MutableFuelRestMassData> = historyList.takeLast(actualTurn)

            val latestProductionFuel: Double = subHistory.takeLast(actualCompareTurn).sumOf {
                it.production
            } / actualCompareTurn

            val previousProductionFuel: Double  = subHistory.take(
                actualTurn - actualCompareTurn
            ).sumOf {
                it.production
            } / (actualTurn - actualCompareTurn)

            latestProductionFuel > previousProductionFuel * compareMultiplier
        }
    }

    companion object {
        val logger = RelativitizationLogManager.getLogger()
    }
}