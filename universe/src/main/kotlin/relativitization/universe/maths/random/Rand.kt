package relativitization.universe.maths.random

import kotlinx.datetime.Clock
import kotlin.random.Random

object Rand {
    private var default = Random(Clock.System.now().epochSeconds)

    fun rand() = default
}