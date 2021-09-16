package relativitization.universe.ai.default.utils

import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.maths.sampling.WeightedReservoir
import relativitization.universe.utils.RelativitizationLogManager

abstract class Reasoner : AINode {
    abstract fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planStatus: PlanStatus
    ): List<AINode>
}

abstract class SequenceReasoner : Reasoner() {

    protected open fun updateStatus(
        planDataAtPlayer: PlanDataAtPlayer, planStatus: PlanStatus
    ) {}

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planStatus: PlanStatus) {
        logger.debug("${this::class.simpleName} (SequenceReasoner) updating data")

        val subNodeList: List<AINode> = getSubNodeList(planDataAtPlayer, planStatus)
        subNodeList.forEach { it.updatePlan(planDataAtPlayer, planStatus) }

        updateStatus(planDataAtPlayer, planStatus)
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

abstract class DualUtilityReasoner : Reasoner() {
    abstract fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planStatus: PlanStatus
    ): List<Option>

    protected open fun updateStatus(
        planDataAtPlayer: PlanDataAtPlayer, planStatus: PlanStatus
    ) {}

    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planStatus: PlanStatus
    ): List<AINode> {
        return getOptionList(planDataAtPlayer, planStatus)
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planStatus: PlanStatus) {
        logger.debug("${this::class.java.simpleName} (DualUtilityReasoner) updating data")

        val selectedOption: Option = selectOption(planDataAtPlayer, planStatus)

        selectedOption.updatePlan(planDataAtPlayer, planStatus)

        updateStatus(planDataAtPlayer, planStatus)
    }

    protected fun selectOption(planDataAtPlayer: PlanDataAtPlayer, planStatus: PlanStatus): Option {
        val optionList: List<Option> = getOptionList(planDataAtPlayer, planStatus)
        val optionWeightMap: Map<Option, Double> = optionList.associateWith {
            it.getWeight(planDataAtPlayer, planStatus)
        }
        val validOptionWeightMap: Map<Option, Double> = optionWeightMap.filterValues { it > 0.0 }

        return if (validOptionWeightMap.isNotEmpty()) {
            val maxRank: Int = validOptionWeightMap.maxOfOrNull {
                it.key.getRank(planDataAtPlayer, planStatus)
            }!!

            val maxRankValidOptionWeightMap: Map<Option, Double> = validOptionWeightMap.filterKeys {
                it.getRank(planDataAtPlayer, planStatus) == maxRank
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