package relativitization.universe.maths.physics

import kotlin.math.abs
import kotlin.test.Test

internal class VelocityTest {
    @Test
    fun perpendicularUnitVelocityTest() {
        val v1 = Velocity(1.0, 2.0, 3.0)
        val (u11, u12) = v1.perpendicularUnitVectorPair()

        assert(abs(1.0 - u11.mag()) < 0.00001)
        assert(abs(1.0 - u12.mag()) < 0.00001)
        assert(v1.dot(u11) < 0.00001)
        assert(v1.dot(u12) < 0.00001)
        assert(u11.dot(u12) < 0.00001)

        val v2 = Velocity(192.0, -112.0, -431.0)
        val (u21, u22) = v2.perpendicularUnitVectorPair()

        assert(abs(1.0 - u21.mag()) < 0.00001)
        assert(abs(1.0 - u22.mag()) < 0.00001)
        assert(v2.dot(u21) < 0.00001)
        assert(v2.dot(u22) < 0.00001)
        assert(u21.dot(u22) < 0.00001)
    }
}