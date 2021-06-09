package relativitization.universe.maths.physics

import relativitization.universe.data.physics.Velocity
import relativitization.universe.maths.physics.Relativistic.canTargetVelocityByPhotonRocket
import relativitization.universe.maths.physics.Relativistic.targetVelocityByPhotonRocket
import kotlin.test.Test

internal class RelativisticTest {
    @Test
    fun canTargetVelocityTest() {
        val b1: Boolean = canTargetVelocityByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.1,
            initialVelocity = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            targetDirection = Velocity(vx = 0.4, vy = 0.4, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(b1)

        val b2: Boolean = canTargetVelocityByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.1,
            initialVelocity = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            targetDirection = Velocity(vx = 0.4, vy = -0.4, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(!b2)

        val b3: Boolean = canTargetVelocityByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.1,
            initialVelocity = Velocity(vx = 0.0, vy = 0.0, vz = 0.0),
            targetDirection = Velocity(vx = 0.4, vy = -0.4, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(b3)

        val b4: Boolean = canTargetVelocityByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.5,
            initialVelocity = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            targetDirection = Velocity(vx = 0.4, vy = -0.4, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(b4)

        val b5: Boolean = canTargetVelocityByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.1,
            initialVelocity = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            targetDirection = Velocity(vx = 0.4, vy = 0.3, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(b5)
    }

    @Test
    fun targetVelocityTest() {
        val v1: Velocity = targetVelocityByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.6,
            initialVelocity = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            targetDirection = Velocity(vx = -0.4, vy = -0.4, vz = -0.4),
            accelerate = true,
            speedOfLight = 1.0
        )

        println(v1)

        val v2: Velocity = targetVelocityByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.6,
            initialVelocity = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            targetDirection = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            accelerate = true,
            speedOfLight = 1.0
        )

        println(v2)
    }
}