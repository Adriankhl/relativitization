package relativitization.universe.maths.algebra

object Piecewise {
    /**
     * Combining quadratic and logistic function
     *
     * @param x x value from 0 to infinity
     * @param yMax maximum value of y
     */
    fun quadLogistic(
        x: Double,
        yMax: Double,
    ): Double = when {
        x < 0.0 -> 0.0
        x < 1.0 -> Quadratic.standard(
            x = x,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.0,
            yMax = 1.0,
            increasing = true,
            accelerate = true
        )
        else -> Logistic.scaledLogistic(
            x = x - 1.0,
            slope0 = 2.0,
            yMax = (yMax - 1.0) * 2.0,
        ) - (yMax - 1.0) + 1.0
    }
}