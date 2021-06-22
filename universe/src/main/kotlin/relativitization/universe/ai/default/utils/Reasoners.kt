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
        return if (optionList.isEmpty()) {
            listOf()
        } else {

            val rankMap: Map<Option, Int> = optionList.associateWith { it.getRank() }

            val maxRank: Int = rankMap.values.maxOrNull()!!

            val optionWaitList: List<Option> = rankMap.filterValues { it == maxRank }.keys.toList()
            val weightList: List<Double> = optionWaitList.map { it.getWeight() }

            // Iterate to select option by weight
            var selectedOption: Option = optionWaitList[0]
            val totalWeight: Double = weightList.sum()
            var optionRand: Double = Random.Default.nextDouble() * totalWeight
            for (i in (0 until weightList.size)) {
                optionRand -= weightList[i]
                if (optionRand <= 0.0) {
                    selectedOption = optionWaitList[i]
                    break
                }
            }

            selectedOption.getCommandList()
        }
    }
}