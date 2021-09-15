package relativitization.universe.ai.default.utils

import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.maths.sampling.WeightedReservoir
import relativitization.universe.utils.RelativitizationLogManager

abstract class Reasoner : AINode {
    abstract fun getSubNodeList(): List<AINode>
}

abstract class SequenceReasoner : Reasoner() {
    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planStatus: PlanStatus) {
        logger.debug("${this::class.simpleName} (SequenceReasoner) updating data")

        val subNodeList: List<AINode> = getSubNodeList()
        subNodeList.forEach { it.updatePlan(planDataAtPlayer, planStatus) }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

abstract class DualUtilityReasoner : Reasoner() {
    abstract fun getOptionList(): List<Option>
    override fun getSubNodeList(): List<AINode> = getOptionList()

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planStatus: PlanStatus) {
        logger.debug("${this::class.java.simpleName} (DualUtilityReasoner) updating data")

        val optionList: List<Option> = getOptionList()
        val optionWeightMap: Map<Option, Double> = optionList.associateWith { it.getWeight() }
        val validOptionWeightMap: Map<Option, Double> = optionWeightMap.filterValues { it > 0.0 }

        if (validOptionWeightMap.isNotEmpty()) {
            val maxRank: Int = validOptionWeightMap.maxOfOrNull { it.key.getRank() }!!

            val maxRankValidOptionWeightMap: Map<Option, Double> = validOptionWeightMap.filterKeys {
                it.getRank() == maxRank
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