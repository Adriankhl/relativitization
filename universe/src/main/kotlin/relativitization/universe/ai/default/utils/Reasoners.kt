package relativitization.universe.ai.default.utils

import relativitization.universe.utils.RelativitizationLogManager
import kotlin.random.Random

abstract class Reasoner : Option {
    abstract fun getOptionList(): List<Option>
}

abstract class SequenceReasoner : Reasoner() {
    override fun updateData() {
        val className = this::class.qualifiedName

        logger.debug("$className (SequenceReasoner) updating data")

        val optionList = getOptionList()
        optionList.forEach { it.updateData() }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

abstract class DualUtilityReasoner : Reasoner() {
    override fun updateData() {
        val className = this::class.qualifiedName

        logger.debug("$className (DualUtilityReasoner) updating data")

        val optionList = getOptionList()
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

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

abstract class RepeatUntilReasoner : Reasoner() {

    // Whether the reasoner should continue looping
    abstract fun shouldContinue(): Boolean

    // Tick after each updateData() in option
    abstract fun tick(option: Option)

    override fun updateData() {
        val className = this::class.qualifiedName

        logger.debug("$className (RepeatUntilReasoner) updating data")

        while (shouldContinue()) {
            logger.debug("$className (RepeatUntilReasoner) repeating")
            val optionList = getOptionList()
            for (option in optionList) {
                if (shouldContinue()) {
                    option.updateData()
                    tick(option)
                } else {
                    break
                }
            }
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}