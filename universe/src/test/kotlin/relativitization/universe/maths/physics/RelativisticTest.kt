package relativitization.universe.maths.physics

import relativitization.universe.data.physics.Velocity
import relativitization.universe.maths.physics.Relativistic.canChangeVelocityByPhotonRocket
import kotlin.test.Test

internal class RelativisticTest {
    @Test
    fun canChangeVelocityTest() {
        val b1: Boolean = canChangeVelocityByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.1,
            initialVelocity = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            targetDirection = Velocity(vx = 0.4, vy = 0.4, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(b1)

        val b2: Boolean = canChangeVelocityByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.1,
            initialVelocity = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            targetDirection = Velocity(vx = 0.4, vy = -0.4, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(!b2)

        val b3: Boolean = canChangeVelocityByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.1,
            initialVelocity = Velocity(vx = 0.0, vy = 0.0, vz = 0.0),
            targetDirection = Velocity(vx = 0.4, vy = -0.4, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(b3)

        val b4: Boolean = canChangeVelocityByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.5,
            initialVelocity = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            targetDirection = Velocity(vx = 0.4, vy = -0.4, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(b4)
    }
}