package relativitization.universe.core.maths.sampling

import relativitization.universe.core.utils.RelativitizationLogManager
import kotlin.random.Random

object WeightedSample {
    private val logger = RelativitizationLogManager.getLogger()

    /**
     * Weighted sample from a list
     *
     * @param numItem number of items to return
     * @param itemList the list to be sorted
     * @param random random number generator
     * @param weightFunction function turning item to weight
     *
     * @return a list of items from weighted sampling
     */
    fun <T> sample(
        numItem: Int,
        itemList: List<T>,
        random: Random,
        weightFunction: (T) -> Double,
    ): List<T> {
        val validItemList: List<T> = itemList.filter {
            weightFunction(it) >= 0
        }

        return if (validItemList.isEmpty()) {
            logger.debug("Sample from random list")
            listOf()
        } else {
            val weightList: List<Double> = validItemList.drop(1).fold(
                listOf(weightFunction(validItemList.first()))
            ) { acc, t ->
                val newValue: Double = acc.last() + weightFunction(t)
                acc + newValue
            }

            val totalWeight: Double = weightList.last()

            return List(numItem) {
                val randomDouble: Double = random.nextDouble(0.0, totalWeight)
                val itemIndex: Int = weightList.indexOfFirst {
                    randomDouble < it
                }

                validItemList[itemIndex]
            }
        }
    }
}