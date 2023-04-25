package relativitization.universe.game.maths.algebra

import kotlin.math.exp
import kotlin.math.ln


object Logistic {
    fun logit(p: Double) = ln(p / (1.0 - p))

    fun standardLogistic(x: Double) = 1.0 / (1.0 + exp(-x))

    /**
     * A scaled logistic function where it has a specific slope at x = 0 and the max y
     *
     * @param x x value
     * @param slope0 slope at x = 0
     * @param yMax the maximum of y
     */
    fun scaledLogistic(
        x: Double,
        slope0: Double,
        yMax: Double,
    ): Double = yMax * standardLogistic(x * slope0 / (0.25 * yMax))
}