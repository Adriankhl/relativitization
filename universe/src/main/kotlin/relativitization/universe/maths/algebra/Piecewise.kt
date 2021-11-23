package relativitization.universe.maths.algebra

object Piecewise {
    /**
     * Combining quadratic and logistic function
     *
     * @param x x value from 0 to infinity
     * @param yMin minimum value of y, should be negative
     * @param yMax maximum value of y, should be positive
     * @param logisticSlope1 slope of the logistic curve at x = 1, default having a smooth derivative
     */
    fun quadLogistic(
        x: Double,
        yMin: Double,
        yMax: Double,
        logisticSlope1: Double = -yMin * 2.0
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
        else -> Logistic.scaledLogistic(
            x = x - 1.0,
            slope0 = logisticSlope1,
            yMax = yMax * 2.0,
        ) - yMax
    }
}