package relativitization.universe.maths.sampling

import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.pow
import kotlin.random.Random

object WeightedReservoir {
    private val logger = RelativitizationLogManager.getLogger()

    /**
     * Sample from a reservoir by the A-Res algorithm
     * Note that this is not the most optimal algorithm
     * https://en.wikipedia.org/wiki/Reservoir_sampling#Algorithm_A-Res
     *
     * @param numItem number of items to return
     * @param itemList the list to be sorted
     * @param weightFunction function turning item to weight
     *
     * @return a list of items from weighted sampling
     */
    fun <T> aRes(
        numItem: Int,
        itemList: List<T>,
        weightFunction: (T) -> Double,
    ): List<T> {
        if (itemList.size < numItem) {
            logger.error("number of items smaller then the size of the list to be sampled")
        }

        val pairList: List<Pair<T, Double>> = itemList.map {
            val weight: Double = weightFunction(it)
            val random: Double = Random.Default.nextDouble()
            it to random.pow(1.0 / weight)
        }

        val sortedList: List<Pair<T, Double>> = pairList.sortedBy {
            it.second
        }

        return sortedList.takeLast(numItem).map { it.first }
    }
}