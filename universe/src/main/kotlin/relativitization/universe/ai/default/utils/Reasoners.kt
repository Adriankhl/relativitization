package relativitization.universe.ai.default.utils

import relativitization.universe.data.commands.Command
import kotlin.random.Random

abstract class Reasoner : Option {
    abstract val optionList: List<Option>
}

abstract class SequenceReasoner(protected val decisionData: DecisionData) : Reasoner() {
    override fun updateData() {
        optionList.forEach { it.updateData() }
    }
}

abstract class DualUtilityReasoner(val decisionData: DecisionData): Reasoner() {
    override fun updateData() {
        val optionWeightMap: Map<Option, Double> = optionList.associateWith { it.getWeight() }
        val validOptionWeightMap: Map<Option, Double> = optionWeightMap.filterValues { it > 0.0 }

        if (validOptionWeightMap.isNotEmpty()) {

            val optionRankMap: Map<Option, Int> = validOptionWeightMap.keys.associateWith { it.getRank() }

            val maxRank: Int = optionRankMap.values.maxOrNull()!!

            val maxRankOptionList: Set<Option> = optionRankMap.filterValues { it == maxRank }.keys

            val maxRankOptionWeightMap: Map<Option, Double> = optionWeightMap.filterKeys {
                maxRankOptionList.contains(it)
            }

            // Iterate to select one option by weight
            val totalWeight: Double = maxRankOptionWeightMap.values.sum()
            var optionRand: Double = Random.Default.nextDouble() * totalWeight
            for ((option, weight) in maxRankOptionWeightMap) {
                optionRand -= weight
                if (optionRand <= 0.0) {
                    option.updateData()
                    break
                }
            }
        }
    }
}