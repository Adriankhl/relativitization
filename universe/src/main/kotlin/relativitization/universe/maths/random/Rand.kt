package relativitization.universe.maths.random

import kotlin.random.Random
import kotlin.system.measureNanoTime

object Rand {
    private var default = Random(measureNanoTime { println("Get default time") })

    fun rand() = default
}