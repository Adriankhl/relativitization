package relativitization.universe.maths.algebra

import kotlin.math.tanh

object Piecewise {
    /**
     * Combining quadratic and hyperbolic tan function
     *
     * @param x x value from 0 to infinity
     * @param yMin minimum value of y, should be negative
     * @param yMax maximum value of y, should be positive
     * @param tanhSlope1 slope of the logistic curve at x = 1, default having a smooth derivative
     */
    fun quadTanh(
        x: Double,
        yMin: Double,
        yMax: Double,
        tanhSlope1: Double = -yMin * 2.0
    ): Double = when {
        x < 0.0 -> 0.0
        x < 1.0 -> Quadratic.standard(
            x = x,
            xMin = 0.0,
            xMax = 1.0,
            yMin = yMin,
            yMax = 0.0,
            increasing = true,
            accelerate = true
        )
        else -> tanh((x - 1.0) * tanhSlope1 / yMax) * yMax
    }
}