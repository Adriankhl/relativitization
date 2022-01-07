package relativitization.universe.ai.defaults.consideration.carrier

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.DualUtilityDataFactory
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.defaults.popsystem.pop.PopType

/**
 * Whether this player has sufficient population compare to ideal population
 *
 * @param ratio the required ratio of total population compare to ideal population
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 */
class SufficientPopulationRatioConsideration(
    private val ratio: Double,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val totalIdealPopulation: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.fold(
                0.0
            ) { acc, mutableCarrierData ->
                acc + mutableCarrierData.carrierInternalData.idealPopulation
            }

        val totalPopulation: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.fold(
                0.0
            ) { acc, mutableCarrierData ->
                acc + PopType.values().sumOf { popType ->
                    mutableCarrierData.allPopData.getCommonPopData(popType).adultPopulation
                }
            }

        return if (totalPopulation < totalIdealPopulation * ratio) {
            DualUtilityDataFactory.noImpact()
        } else {
            DualUtilityData(rank = rankIfTrue, multiplier = multiplierIfTrue, bonus = bonusIfTrue)
        }
    }
}