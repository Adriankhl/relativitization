package relativitization.universe.game.maths.number

import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sign

object Notation {
    /**
     * Round to number to a specific decimal place
     */
    fun roundDecimal(num: Double, decimalPlace: Int): Double {
        val multiplier: Double = 10.0.pow(decimalPlace)
        return round(num * multiplier) / multiplier
    }
}


/**
 * Turn a double number to scientific notation
 */
fun Double.toScientificNotation(): ScientificNotation {
    return if (this != 0.0) {
        val sign: Double = this.sign
        val logNum: Double = log10(this * sign)
        val exponent: Int = floor(logNum).toInt()
        val coefficient: Double = sign * 10.0.pow(logNum - exponent)

        ScientificNotation(coefficient, exponent)
    } else {
        ScientificNotation(0.0, 0)
    }
}

/**
 * Scientific notation in the form of m * 10^n
 */
data class ScientificNotation(
    val coefficient: Double,
    val exponent: Int,
) {
    /**
     * Use string to double instead of multiplying to avoid floating point error
     */
    fun toDouble(): Double = "${coefficient}E${exponent}".toDouble()

    fun toDouble(decimalPlace: Int): Double =
        "${Notation.roundDecimal(coefficient, decimalPlace)}E${exponent}".toDouble()

    fun toString(decimalPlace: Int): String =
        "${Notation.roundDecimal(coefficient, decimalPlace)}E${exponent}"
}