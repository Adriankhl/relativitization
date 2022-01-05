package relativitization.universe.ai.defaults.consideration.building

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.DualUtilityDataFactory
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.data.components.defaults.popsystem.pop.engineer.laboratory.MutableLaboratoryData
import relativitization.universe.data.components.defaults.science.knowledge.AppliedResearchProjectData
import relativitization.universe.maths.physics.Intervals

/**
 * Check if there is no laboratory at a carrier
 *
 * @property carrierId the id of the carrier
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 */
class NoLaboratoryAtCarrierConsideration(
    private val carrierId: Int,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val hasLaboratory: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId).allPopData
            .engineerPopData.laboratoryMap.isNotEmpty()

        return if (hasLaboratory) {
            DualUtilityDataFactory.noImpact()
        } else {
            DualUtilityData(rank = rankIfTrue, multiplier = multiplierIfTrue, bonus = bonusIfTrue)
        }
    }
}

/**
 * Check if there is fewer than or equal to one laboratory at carrier
 *
 * @property carrierId the id of the carrier
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 */
class OnlyOneLaboratoryConsideration(
    private val carrierId: Int,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val numLaboratory: Int = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId).allPopData
            .engineerPopData.laboratoryMap.size

        return if (numLaboratory > 1) {
            DualUtilityDataFactory.noImpact()
        } else {
            DualUtilityData(rank = rankIfTrue, multiplier = multiplierIfTrue, bonus = bonusIfTrue)
        }
    }
}

/**
 * Check if institute is sufficient to cover scholar employment
 *
 * @property carrierId the id of the carrier to consider
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 * @property rankIfFalse rank of dual utility if this is false
 * @property multiplierIfFalse multiplier of dual utility if this is false
 * @property bonusIfFalse bonus of dual utility if this is false
 */
class SufficientLaboratoryConsideration(
    private val carrierId: Int,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
    private val rankIfFalse: Int,
    private val multiplierIfFalse: Double,
    private val bonusIfFalse: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val carrier: MutableCarrierData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId)

        val laboratoryList: List<MutableLaboratoryData> =
            carrier.allPopData.engineerPopData.laboratoryMap.values.toList()

        val totalMaxEmployee: Double = laboratoryList.fold(0.0){ acc, laboratory ->
            acc + laboratory.laboratoryInternalData.maxNumEmployee
        }

        val totalEngineerPopulation: Double =
            carrier.allPopData.engineerPopData.commonPopData.adultPopulation

        // Sufficient if institute position is more than total engineer population
        return if (totalMaxEmployee >= totalEngineerPopulation) {
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
 * Check if the laboratory is doing any known applied research project
 *
 * @property carrierId the id of the carrier to consider
 * @property laboratoryId the id of the laboratory
 * @property rankIfTrue rank of dual utility if this is true
 * @property multiplierIfTrue multiplier of dual utility if this is true
 * @property bonusIfTrue bonus of dual utility if this is true
 */
class KnownAppliedProjectInRangeConsideration(
    private val carrierId: Int,
    private val laboratoryId: Int,
    private val rankIfTrue: Int,
    private val multiplierIfTrue: Double,
    private val bonusIfTrue: Double,
) : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val laboratory: MutableLaboratoryData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId).allPopData
            .engineerPopData.laboratoryMap.getValue(laboratoryId)

        val knownAppliedProjectList: List<AppliedResearchProjectData> = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.playerScienceData()
            .knownAppliedResearchProjectList

        val anyInRange: Boolean = knownAppliedProjectList.any {
            Intervals.distance(
                laboratory.laboratoryInternalData.xCor,
                laboratory.laboratoryInternalData.yCor,
                it.xCor,
                it.yCor
            ) <= laboratory.laboratoryInternalData.range
        }

        // Sufficient if institute position is more than total scholar population
        return if (anyInRange) {
            DualUtilityData(
                rank = rankIfTrue,
                multiplier = multiplierIfTrue,
                bonus = bonusIfTrue
            )
        } else {
            DualUtilityDataFactory.noImpact()
        }
    }
}