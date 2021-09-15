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
    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planStatus: PlanStatus) {
        logger.debug("${this::class.simpleName} (SequenceReasoner) updating data")

        val subNodeList: List<AINode> = getSubNodeList(planDataAtPlayer, planStatus)
        subNodeList.forEach { it.updatePlan(planDataAtPlayer, planStatus) }
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

    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planStatus: PlanStatus
    ): List<AINode> {
        return getOptionList(planDataAtPlayer, planStatus)
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planStatus: PlanStatus) {
        logger.debug("${this::class.java.simpleName} (DualUtilityReasoner) updating data")

        val optionList: List<Option> = getOptionList(planDataAtPlayer, planStatus)
        val optionWeightMap: Map<Option, Double> = optionList.associateWith {
            it.getWeight(planDataAtPlayer, planStatus)
        }
        val validOptionWeightMap: Map<Option, Double> = optionWeightMap.filterValues { it > 0.0 }

        if (validOptionWeightMap.isNotEmpty()) {
            val maxRank: Int = validOptionWeightMap.maxOfOrNull {
                it.key.getRank(planDataAtPlayer, planStatus)
            }!!

            val maxRankValidOptionWeightMap: Map<Option, Double> = validOptionWeightMap.filterKeys {
                it.getRank(planDataAtPlayer, planStatus) == maxRank
            }

            val selectedOption: Option = WeightedReservoir.aRes(
                1,
                maxRankValidOptionWeightMap.keys.toList(),
            ) {
                maxRankValidOptionWeightMap.getValue(it)
            }.first()

            selectedOption.updatePlan(planDataAtPlayer, planStatus)
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}