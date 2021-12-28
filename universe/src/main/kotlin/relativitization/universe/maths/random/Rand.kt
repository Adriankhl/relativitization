package relativitization.universe.maths.random

import kotlinx.datetime.Clock
import kotlin.random.Random

object Rand {
    private var default = Random(Clock.System.now().epochSeconds)

    fun rand() = default

    fun setSeed(seed: Long) {
        default = Random(seed)
    }

    /**
     * Transform a list into group, then shuffle the group
     */
    fun <T> groupByAndShuffle(
        list: List<T>,
        groupByFunction: (T) -> Int,
    ): List<T> {
        val listGroup: Map<Int, List<T>> = list.groupBy(groupByFunction)
        return listGroup.values.shuffled(default).flatten()
    }
}