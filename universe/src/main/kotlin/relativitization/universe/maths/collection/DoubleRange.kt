package relativitization.universe.maths.collection

import relativitization.universe.maths.number.Notation

object DoubleRange {
    /**
     * Get a list of Double
     *
     * @param from starting from this number
     * @param to ends on or before this number
     * @param step the step between elements
     * @param decimalPlace decimal place to round for each element
     */
    fun computeList(
        from: Double,
        to: Double,
        step: Double,
        decimalPlace: Int,
        currentList: List<Double> = listOf()
    ): List<Double> {
        return if (from > to) {
            currentList
        } else {
            computeList(
                from = Notation.roundDecimal(from + step, decimalPlace),
                to = to,
                step = step,
                decimalPlace = decimalPlace,
                currentList = currentList + from
            )
        }
    }
}