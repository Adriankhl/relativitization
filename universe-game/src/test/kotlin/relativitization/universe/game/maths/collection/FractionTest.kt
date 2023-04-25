package relativitization.universe.game.maths.collection

import kotlin.test.Test

internal class FractionTest {
    @Test
    fun oneFractionTest() {
        val f1 = Fraction.oneFractionList(
            3,
            0.5
        )

        val f2 = Fraction.oneFractionList(
            5,
            0.5
        )

        assert(f1 == listOf(0.5, 0.25, 0.25))
        assert(f2 == listOf(0.5, 0.25, 0.125, 0.0625, 0.0625))
    }
}