package relativitization.universe.maths.number

import kotlin.math.abs
import kotlin.test.Test

internal class NotationTest {
    @Test
    fun scientificNotationTest() {
        val num1 = 20.0
        val sf1 = num1.toScientificNotation()
        assert(abs(num1 - sf1.toDouble()) < 0.0001 * sf1.toDouble())

        val num2 = 0.0
        val sf2 = num2.toScientificNotation()
        assert(abs(num2 - sf2.toDouble()) < 0.0001)

        val num3 = Double.MAX_VALUE * 0.0001
        val sf3 = num3.toScientificNotation()
        assert(abs(num3 - sf3.toDouble()) < 0.0001 * sf3.toDouble())

        val num4 = Double.MIN_VALUE * 10000.0
        val sf4 = num4.toScientificNotation()
        assert(abs(num4 - sf4.toDouble()) < 0.0001 * sf4.toDouble())

        val num5 = -Double.MAX_VALUE * 0.0001
        val sf5 = num5.toScientificNotation()
        assert(abs(num5 - sf5.toDouble()) < 0.0001 * abs(sf5.toDouble()))

        val num6 = -Double.MIN_VALUE * 10000.0
        val sf6 = num6.toScientificNotation()
        assert(abs(num6 - sf6.toDouble()) < 0.0001 * abs(sf6.toDouble()))

        val num7 = 500.0
        val sf7 = num7.toScientificNotation()
        assert(sf7.exponent == 2)

        val num8 = 5E-6
        val sf8 = num8.toScientificNotation()
        assert(sf8.exponent == -6)
    }
}