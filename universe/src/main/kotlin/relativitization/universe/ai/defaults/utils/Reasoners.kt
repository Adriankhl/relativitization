package relativitization.universe.ai.defaults.utils

import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.maths.sampling.WeightedReservoir
import relativitization.universe.utils.RelativitizationLogManager

abstract class Reasoner : AINode() {
    abstract fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode>
}

abstract class SequenceReasoner : Reasoner() {
    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        logger.debug("${this::class.simpleName} (SequenceReasoner) updating data")

        val subNodeList: List<AINode> = getSubNodeList(planDataAtPlayer, planState)
        subNodeList.forEach { it.updatePlan(planDataAtPlayer, planState) }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

abstract class DualUtilityReasoner : Reasoner() {
    abstract fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption>

    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return getOptionList(planDataAtPlayer, planState)
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        logger.debug("${this::class.simpleName} (DualUtilityReasoner) updating data")

        val selectedDualUtilityOption: DualUtilityOption = selectOption(planDataAtPlayer, planState)

        selectedDualUtilityOption.updatePlan(planDataAtPlayer, planState)
    }

    private fun selectOption(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityOption {
        val dualUtilityOptionList: List<DualUtilityOption> =
            getOptionList(planDataAtPlayer, planState)
        val dualUtilityOptionWeightMap: Map<DualUtilityOption, Double> =
            dualUtilityOptionList.associateWith {
                it.getWeight(planDataAtPlayer, planState)
            }
        val validDualUtilityOptionWeightMap: Map<DualUtilityOption, Double> =
            dualUtilityOptionWeightMap.filterValues { it > 0.0 }

        return if (validDualUtilityOptionWeightMap.isNotEmpty()) {
            val maxRank: Int = validDualUtilityOptionWeightMap.maxOf {
                it.key.getRank(planDataAtPlayer, planState)
            }

            val maxRankValidDualUtilityOptionWeightMap: Map<DualUtilityOption, Double> =
                validDualUtilityOptionWeightMap.filterKeys {
                    it.getRank(planDataAtPlayer, planState) == maxRank
                }

            WeightedReservoir.aRes(
                1,
                maxRankValidDualUtilityOptionWeightMap.keys.toList(),
            ) {
                maxRankValidDualUtilityOptionWeightMap.getValue(it)
            }.first()
        } else {
            EmptyDualUtilityOption()
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}