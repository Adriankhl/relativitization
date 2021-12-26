package relativitization.universe.ai.defaults.consideration.construction

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.defaults.economy.ResourceType

/**
 * Check if there is not such a resource library
 *
 * @property trueRank rank of dual utility if this is true
 * @property trueMultiplier multiplier of dual utility if this is true
 * @property trueBonus bonus of dual utility if this is true
 */
class NoResourceFactoryConsideration(
    val resourceType: ResourceType,
    private val trueRank: Int,
    private val trueMultiplier: Double,
    private val trueBonus: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val hasResourceFactory: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.any {
                it.allPopData.labourerPopData.resourceFactoryMap.values.any {
                    it.resourceFactoryInternalData.outputResource == resourceType
                }
            }

        return if (hasResourceFactory) {
            DualUtilityData(rank = 0, multiplier = 1.0, bonus = 0.0)
        } else {
            DualUtilityData(rank = trueRank, multiplier = trueMultiplier, bonus = trueBonus)
        }
    }
}