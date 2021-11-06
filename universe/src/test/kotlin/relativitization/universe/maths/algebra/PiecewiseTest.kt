package relativitization.universe.maths.algebra

import kotlin.math.pow
import kotlin.test.Test

internal class PiecewiseTest {
    @Test
    fun valueTest() {
        assert((Piecewise.quadLogistic(0.001, 0.0, 5.0).pow(2)) < 0.01)
        assert((Piecewise.quadLogistic(0.999, 0.0, 5.0) - 1.0).pow(2) < 0.01)
        assert((Piecewise.quadLogistic(1.001, 0.0, 5.0) - 1.0).pow(2) < 0.01)
        assert(((Piecewise.quadLogistic(1.001, 0.0, 5.0) - 1.0) / 0.001 - 2.0).pow(2) < 0.01)
        assert((Piecewise.quadLogistic(1E100, 0.0, 5.0) - 5.0).pow(2) < 0.01)
    }
}