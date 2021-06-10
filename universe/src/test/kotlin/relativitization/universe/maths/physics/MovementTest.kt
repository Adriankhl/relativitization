package relativitization.universe.maths.physics

import relativitization.universe.data.physics.Double3D
import relativitization.universe.data.physics.Velocity
import relativitization.universe.maths.physics.Movement.isSameDirection
import kotlin.test.Test

internal class MovementTest {
    @Test
    fun sameDirectionTest() {
        val b1: Boolean = isSameDirection(Velocity(0.3, 0.3, 0.3), Double3D(0.1, 0.1, 0.1))
        assert(b1)
    }
}