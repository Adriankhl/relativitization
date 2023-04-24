package relativitization.universe.core.maths.sampling

import relativitization.universe.core.utils.RelativitizationLogManager
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
     * @param random random number generator
     * @param weightFunction function turning item to weight
     *
     * @return a list of items from weighted sampling
     */
    fun <T> aRes(
        numItem: Int,
        itemList: List<T>,
        random: Random,
        weightFunction: (T) -> Double,
    ): List<T> {
        val validItemList: List<T> = itemList.filter {
            weightFunction(it) >= 0
        }

        return if (validItemList.size < numItem) {
            logger.debug("number of valid items smaller then the size of the list to be sampled")
            validItemList
        } else {
            val pairList: List<Pair<T, Double>> = validItemList.map {
                val weight: Double = weightFunction(it)
                val randomDouble: Double = random.nextDouble()

                it to randomDouble.pow(1.0 / weight)
            }

            val sortedList: List<Pair<T, Double>> = pairList.sortedBy {
                it.second
            }

            sortedList.takeLast(numItem).map { it.first }
        }
    }
}