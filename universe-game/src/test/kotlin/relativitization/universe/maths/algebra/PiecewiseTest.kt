package relativitization.universe.maths.algebra

import kotlin.math.pow
import kotlin.test.Test

internal class PiecewiseTest {
    @Test
    fun valueTest() {
        assert(((Piecewise.quadTanh(0.001, -1.0, 0.0, 5.0) + 1.0).pow(2)) < 0.01)
        assert((Piecewise.quadTanh(0.999, -1.0, 0.0, 5.0) - 0.0).pow(2) < 0.01)
        assert((Piecewise.quadTanh(1.001, -1.0, 0.0, 5.0) - 0.0).pow(2) < 0.01)
        assert(((Piecewise.quadTanh(1.001, -1.0, 0.0, 5.0) - 0.0) / 0.001 - 2.0).pow(2) < 0.01)
        assert((Piecewise.quadTanh(1E100, -1.0, 0.0, 5.0) - 5.0).pow(2) < 0.01)
    }
}