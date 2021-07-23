package relativitization.universe.maths.algebra

import kotlin.math.exp
import kotlin.math.ln


object Logistic {
    fun logit(p: Double) = ln(p / (1.0 - p))

    fun standardLogistic(x: Double) = 1.0 / (1.0 + exp(-x))
}