package relativitization.universe.maths.sampling

import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.pow
import relativitization.universe.maths.random.Rand

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

        return if (itemList.size < numItem) {
            logger.debug("number of items smaller then the size of the list to be sampled")
            itemList
        } else {
            val pairList: List<Pair<T, Double>> = itemList.map {
                val weight: Double = weightFunction(it)
                val random: Double = Rand.rand().nextDouble()

                if (weight <= 0.0) {
                    logger.error("Weight smaller than 0.0, setting weightto 1E-9")
                    it to random.pow(1.0 / 1E-9)
                } else {
                    it to random.pow(1.0 / weight)
                }
            }

            val sortedList: List<Pair<T, Double>> = pairList.sortedBy {
                it.second
            }

            sortedList.takeLast(numItem).map { it.first }
        }
    }
}