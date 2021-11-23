package relativitization.universe.ai.default.utils

import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.maths.sampling.WeightedReservoir
import relativitization.universe.utils.RelativitizationLogManager

abstract class Reasoner : AINode {
    abstract fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode>
}

abstract class SequenceReasoner : Reasoner() {

    protected open fun updateStatus(
        planDataAtPlayer: PlanDataAtPlayer, planState: PlanState
    ) {
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        logger.debug("${this::class.simpleName} (SequenceReasoner) updating data")

        val subNodeList: List<AINode> = getSubNodeList(planDataAtPlayer, planState)
        subNodeList.forEach { it.updatePlan(planDataAtPlayer, planState) }

        updateStatus(planDataAtPlayer, planState)
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

abstract class DualUtilityReasoner : Reasoner() {
    abstract fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<Option>

    protected open fun updateStatus(
        planDataAtPlayer: PlanDataAtPlayer, planState: PlanState
    ) {
    }

    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return getOptionList(planDataAtPlayer, planState)
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        logger.debug("${this::class.java.simpleName} (DualUtilityReasoner) updating data")

        val selectedOption: Option = selectOption(planDataAtPlayer, planState)

        selectedOption.updatePlan(planDataAtPlayer, planState)

        updateStatus(planDataAtPlayer, planState)
    }

    private fun selectOption(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState): Option {
        val optionList: List<Option> = getOptionList(planDataAtPlayer, planState)
        val optionWeightMap: Map<Option, Double> = optionList.associateWith {
            it.getWeight(planDataAtPlayer, planState)
        }
        val validOptionWeightMap: Map<Option, Double> = optionWeightMap.filterValues { it > 0.0 }

        return if (validOptionWeightMap.isNotEmpty()) {
            val maxRank: Int = validOptionWeightMap.maxOf {
                it.key.getRank(planDataAtPlayer, planState)
            }

            val maxRankValidOptionWeightMap: Map<Option, Double> = validOptionWeightMap.filterKeys {
                it.getRank(planDataAtPlayer, planState) == maxRank
            }

            WeightedReservoir.aRes(
                1,
                maxRankValidOptionWeightMap.keys.toList(),
            ) {
                maxRankValidOptionWeightMap.getValue(it)
            }.first()
        } else {
            EmptyOption()
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}