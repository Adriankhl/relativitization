package relativitization.universe.maths.physics

import org.junit.jupiter.api.TestFactory
import relativitization.universe.data.physics.Double3D
import relativitization.universe.data.physics.Velocity
import relativitization.universe.maths.physics.Movement.isSameDirection
import relativitization.universe.maths.physics.Movement.stoppingDistanceByPhotonRocket
import relativitization.universe.maths.physics.Relativistic.targetVelocityByPhotonRocket
import kotlin.test.Test

internal class MovementTest {
    @Test
    fun sameDirectionTest() {
        val b1: Boolean = isSameDirection(Velocity(0.3, 0.3, 0.3), Double3D(0.1, 0.1, 0.1))
        assert(b1)


        val v2 = targetVelocityByPhotonRocket(
            initialRestMass = 1.0,
            maxDeltaRestMass = 0.5,
            initialVelocity = Velocity(vx = 0.2, vy = 0.1, vz = 0.3),
            targetVelocity = Velocity(vx = 0.234, vy = -0.182, vz = -0.411),
            speedOfLight = 1.0
        )

        val b2 = isSameDirection(v2.newVelocity, Double3D(0.234, -0.182, -0.411))
        assert(b2)
    }

    @Test
    fun stoppingDistanceTest() {
        val d1: Double = stoppingDistanceByPhotonRocket(
            initialRestMass = 1.0,
            maxDeltaRestMass = 0.01,
            initialVelocity = Velocity(vx = 0.2, vy = 0.1, vz = 0.3),
            speedOfLight = 1.0
        )

        assert(d1 > 6.4 && d1 < 6.5)
    }
}