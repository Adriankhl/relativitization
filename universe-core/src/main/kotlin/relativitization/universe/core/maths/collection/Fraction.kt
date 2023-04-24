package relativitization.universe.core.maths.collection

object Fraction {
    /**
     * Divide one into exponentially decreasing fraction
     * (0.5, 0.25, 0.125, ...) when keepFraction = 0.5
     *
     * @param num how long should the list be
     * @param keepFraction how large the fraction is kept
     */
    fun oneFractionList(
        num: Int,
        keepFraction: Double,
        last: Double = 1.0,
        fractionList: List<Double> = listOf()
    ): List<Double> {
        return if (num <= 1) {
            fractionList + last
        } else {
            oneFractionList(
                num - 1,
                keepFraction,
                last * (1.0 - keepFraction),
                fractionList + (last * keepFraction)
            )
        }
    }
}