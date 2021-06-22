package relativitization.universe.ai.default.utils

import relativitization.universe.data.commands.Command
import kotlin.random.Random

interface Reasoner {
    val optionList: List<Option>

    fun getCommandList(): List<Command>
}

abstract class SequenceReasoner : Reasoner {
    override fun getCommandList(): List<Command> {
        return optionList.flatMap { it.getCommandList() }
    }
}

abstract class DualUtilityReasoner : Reasoner {
    override fun getCommandList(): List<Command> {
        val optionWeightMap: Map<Option, Double> = optionList.associateWith { it.getWeight() }
        val validOptionWeightMap: Map<Option, Double> = optionWeightMap.filterValues { it > 0.0 }

        return if (validOptionWeightMap.isEmpty()) {
            listOf()
        } else {

            val optionRankMap: Map<Option, Int> = validOptionWeightMap.keys.associateWith { it.getRank() }

            val maxRank: Int = optionRankMap.values.maxOrNull()!!

            val maxRankOptionList: Set<Option> = optionRankMap.filterValues { it == maxRank }.keys

            val maxRankOptionWeightMap: Map<Option, Double> = optionWeightMap.filterKeys {
                maxRankOptionList.contains(it)
            }

            // Iterate to select option by weight
            var selectedOption: Option = maxRankOptionWeightMap.keys.first()
            val totalWeight: Double = maxRankOptionWeightMap.values.sum()
            var optionRand: Double = Random.Default.nextDouble() * totalWeight
            for ((option, weight) in maxRankOptionWeightMap) {
                optionRand -= weight
                if (optionRand <= 0.0) {
                    selectedOption = option
                    break
                }
            }

            selectedOption.getCommandList()
        }
    }
}