package relativitization.universe.maths.physics

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import relativitization.universe.data.physics.Int3D
import relativitization.universe.maths.physics.Intervals.intDelay

internal class IntervalsTest {
    @Test
    fun testIntDelay() {
        val c1 = Int3D(1, 1, 1)
        val c2 = Int3D(1, 1, 1)
        assert(intDelay(c1, c2, 1) == 2)

        val c3 = Int3D(0, 0, 0)
        val c4 = Int3D(0, 0, 1)
        assert(intDelay(c3, c4, 1) == 3)

        val c5 = Int3D(0, 0, 0)
        val c6 = Int3D(1, 1, 1)
        assert(intDelay(c5, c6, 1) == 4)
    }
}