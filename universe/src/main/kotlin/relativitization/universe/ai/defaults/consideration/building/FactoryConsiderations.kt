package relativitization.universe.ai.defaults.consideration.building

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.defaults.economy.ResourceType

/**
 * Check if there is not such a resource library
 *
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 */
class NoResourceFactoryConsideration(
    val resourceType: ResourceType,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
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
            DualUtilityData(rank = rankIfTrue, multiplier = multiplierIfTrue, bonus = bonusIfTrue)
        }
    }
}