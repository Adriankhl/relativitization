package relativitization.universe.maths.number

import kotlin.math.*

object Notation {
    /**
     * Round to number to a specific decimal place
     */
    fun roundDecimal(num: Double, decimalPlace: Int): Double {
        val multiplier: Double = 10.0.pow(decimalPlace)
        return round(num * multiplier) / multiplier
    }

    /**
     * Turn a double number to scientific notation
     */
    fun toScientificNotation(num: Double): ScientificNotation {
        return if (num != 0.0) {
            val sign: Double = num.sign
            val logNum: Double = log10(num * sign)
            val exponent: Int = logNum.toInt()
            val coefficient: Double = sign * 10.0.pow(logNum - exponent)

            ScientificNotation(coefficient, exponent)
        } else {
            toScientificNotation(1.0 / Double.MAX_VALUE)
        }
    }
}

/**
 * Scientific notation in the form of m * 10^n
 */
data class ScientificNotation(
    val coefficient: Double,
    val exponent: Int,
) {
    fun toDouble(): Double = "${coefficient}E${exponent}".toDouble()
}