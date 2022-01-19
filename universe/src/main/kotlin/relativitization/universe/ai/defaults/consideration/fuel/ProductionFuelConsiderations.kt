package relativitization.universe.ai.defaults.consideration.fuel

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.defaults.ai.MutableFuelRestMassHistoryData
import relativitization.universe.data.components.defaults.physics.MutableFuelRestMassData
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType

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
 * @property carrierId the id of the carrier
 * @property popType consider the salary of this pop type
 * @property productionFuelRatio compare salary to production fuel times this ratio
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class PopulationSavingHighCompareToProduction(
    private val carrierId: Int,
    private val popType: PopType,
    private val productionFuelRatio: Double,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration() {
    override fun getDualUtilityData(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState): DualUtilityData {
        val totalPopulation: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.popSystemData()
            .totalAdultPopulation()

        val commonPopData: MutableCommonPopData = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .popSystemData().carrierDataMap.getValue(carrierId).allPopData.getCommonPopData(popType)

        // scaled salary to compare production fuel to as if the whole population is having this saving
        val scaledSaving: Double = if (commonPopData.adultPopulation > 0.0) {
            commonPopData.saving / commonPopData.adultPopulation * totalPopulation
        } else {
            0.0
        }

        val productionFuel: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .physicsData().fuelRestMassData.production

        return if (scaledSaving >= productionFuel * productionFuelRatio) {
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