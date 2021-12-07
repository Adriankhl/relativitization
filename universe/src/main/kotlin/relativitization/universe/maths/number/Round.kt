package relativitization.universe.maths.number

import kotlin.math.pow
import kotlin.math.round

object Round {
    fun roundDecimal(num: Double, decimalPlace: Int): Double {
        val multiplier: Double = 10.0.pow(decimalPlace)
        return round(num * multiplier) / multiplier
    }
}