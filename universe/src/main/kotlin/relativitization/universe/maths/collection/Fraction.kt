package relativitization.universe.maths.collection

object Fraction {
    /**
     * Divide one into exponentially decreasing fraction (0.5, 0.25, 0.125, ...)
     *
     * @param num how long should the list be
     */
    fun oneFractionList(
        num: Int,
        last: Double = 1.0,
        fractionList: List<Double> = listOf()
    ): List<Double> {
        return if (num <= 1) {
            fractionList + last
        } else {
            oneFractionList(
                num - 1,
                last * 0.5,
                fractionList + (last * 0.5)
            )
        }
    }
}