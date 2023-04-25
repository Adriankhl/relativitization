package relativitization.universe.game.maths.physics

import relativitization.universe.game.maths.physics.Int3D
import relativitization.universe.game.maths.physics.Intervals.intDelay
import kotlin.test.Test

internal class IntervalsTest {
    @Test
    fun testIntDelay() {
        val c1 = Int3D(1, 1, 1)
        val c2 = Int3D(1, 1, 1)
        assert(intDelay(c1, c2, 1.0) == 2)

        val c3 = Int3D(0, 0, 0)
        val c4 = Int3D(0, 0, 1)
        assert(intDelay(c3, c4, 1.0) == 3)

        val c5 = Int3D(0, 0, 0)
        val c6 = Int3D(1, 1, 1)
        assert(intDelay(c5, c6, 1.0) == 4)
    }
}