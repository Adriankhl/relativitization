package relativitization.universe.maths.number

import kotlin.math.abs
import kotlin.test.Test

internal class NotationTest {
    @Test
    fun scientificNotationTest() {
        val num1 = 20.0
        val sf1 = Notation.toScientificNotation(num1)
        assert(abs(num1 - sf1.toDouble()) < 0.0001 * sf1.toDouble())

        val num2 = 0.0
        val sf2 = Notation.toScientificNotation(num2)
        assert(abs(num2 - sf2.toDouble()) < 0.0001)

        val num3 = Double.MAX_VALUE * 0.0001
        val sf3 = Notation.toScientificNotation(num3)
        assert(abs(num3 - sf3.toDouble()) < 0.0001 * sf3.toDouble())

        val num4 = Double.MIN_VALUE * 10000.0
        val sf4 = Notation.toScientificNotation(num4)
        assert(abs(num4 - sf4.toDouble()) < 0.0001 * sf4.toDouble())

        val num5 = -Double.MAX_VALUE * 0.0001
        val sf5 = Notation.toScientificNotation(num5)
        println(sf5)
        assert(abs(num5 - sf5.toDouble()) < 0.0001 * abs(sf5.toDouble()))

        val num6 = -Double.MIN_VALUE * 10000.0
        val sf6 = Notation.toScientificNotation(num6)
        println(sf6)
        assert(abs(num6 - sf6.toDouble()) < 0.0001 * abs(sf6.toDouble()))
    }
}