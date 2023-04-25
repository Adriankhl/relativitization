package relativitization.universe.game.ai.defaults.consideration.fuel

import relativitization.universe.game.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.game.ai.defaults.utils.DualUtilityData
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.core.data.PlanDataAtPlayer
import relativitization.universe.game.data.components.aiData
import relativitization.universe.game.data.components.defaults.ai.MutableFuelRestMassHistoryData
import relativitization.universe.game.data.components.defaults.physics.MutableFuelRestMassData
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.popSystemData

/**
 * Whether production fuel is increasing comparing to historical average
 *
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class IncreasingProductionFuelConsideration(
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val fuelHistory: MutableFuelRestMassHistoryData = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.aiData().fuelRestMassHistoryData

        val isIncreasing: Boolean = fuelHistory.isProductionFuelIncreasing(
            turn = fuelHistory.maxStoredTurn,
            turnCompare = fuelHistory.maxStoredTurn - 1,
            compareMultiplier = 1.0,
        )

        return if (isIncreasing) {
            DualUtilityData(
                rank = rankIfTrue,
                multiplier = multiplierIfTrue,
                bonus = bonusIfTrue
            )
        } else {
            DualUtilityData(
                rank = rankIfFalse,
                multiplier = multiplierIfFalse,
                bonus = bonusIfFalse
            )
        }
    }
}

/**
 * Whether production fuel is sufficient
 *
 * @property requiredProductionFuelRestMass compare production fuel to this amount
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class SufficientProductionFuelConsideration(
    private val requiredProductionFuelRestMass: Double,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {

        val fuelData: MutableFuelRestMassData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.physicsData().fuelRestMassData

        return if (fuelData.production >= requiredProductionFuelRestMass) {
            DualUtilityData(
                rank = rankIfTrue,
                multiplier = multiplierIfTrue,
                bonus = bonusIfTrue
            )
        } else {
            DualUtilityData(
                rank = rankIfFalse,
                multiplier = multiplierIfFalse,
                bonus = bonusIfFalse
            )
        }
    }
}


/**
 * Whether population saving is too high compare to production fuel
 *
 * @property productionFuelFactor compare salary to production fuel times this factor
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class PopulationSavingHighCompareToProductionConsideration(
    private val productionFuelFactor: Double,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val totalSaving: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .popSystemData().totalSaving()

        val productionFuel: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .physicsData().fuelRestMassData.production

        return if (totalSaving >= productionFuel * productionFuelFactor) {
            DualUtilityData(
                rank = rankIfTrue,
                multiplier = multiplierIfTrue,
                bonus = bonusIfTrue
            )
        } else {
            DualUtilityData(
                rank = rankIfFalse,
                multiplier = multiplierIfFalse,
                bonus = bonusIfFalse
            )
        }
    }
}