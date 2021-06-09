package relativitization.universe.maths.physics

import relativitization.universe.data.physics.Velocity
import relativitization.universe.maths.physics.Relativistic.canTargetDirectionByPhotonRocket
import relativitization.universe.maths.physics.Relativistic.deltaMassByPhotonRocket
import relativitization.universe.maths.physics.Relativistic.speedByPhotonRocket
import relativitization.universe.maths.physics.Relativistic.targetDirectionByPhotonRocket
import kotlin.test.Test

internal class RelativisticTest {
    @Test
    fun canTargetDirectionTest() {
        val b1: Boolean = canTargetDirectionByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.1,
            initialVelocity = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            targetDirection = Velocity(vx = 0.4, vy = 0.4, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(b1)

        val b2: Boolean = canTargetDirectionByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.1,
            initialVelocity = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            targetDirection = Velocity(vx = 0.4, vy = -0.4, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(!b2)

        val b3: Boolean = canTargetDirectionByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.1,
            initialVelocity = Velocity(vx = 0.0, vy = 0.0, vz = 0.0),
            targetDirection = Velocity(vx = 0.4, vy = -0.4, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(b3)

        val b4: Boolean = canTargetDirectionByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.5,
            initialVelocity = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            targetDirection = Velocity(vx = 0.4, vy = -0.4, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(b4)

        val b5: Boolean = canTargetDirectionByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.1,
            initialVelocity = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            targetDirection = Velocity(vx = 0.4, vy = 0.3, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(b5)
    }

    @Test
    fun targetDirectionTest() {
        val v1: Velocity = targetDirectionByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.1,
            initialVelocity = Velocity(vx = 0.0, vy = 0.0, vz = 0.0),
            targetDirection = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            accelerate = true,
            speedOfLight = 1.0
        )

        val s1: Double = speedByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.1,
            speedOfLight = 1.0
        )

        assert(v1.mag() - s1 < 0.01)

        val v2: Velocity = targetDirectionByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.9,
            initialVelocity = Velocity(vx = 0.0, vy = 0.0, vz = 0.0),
            targetDirection = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            accelerate = true,
            speedOfLight = 1.0
        )

        val s2: Double = speedByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.9,
            speedOfLight = 1.0
        )

        assert(v2.mag() - s2 < 0.01)

        val v3: Velocity = targetDirectionByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.6,
            initialVelocity = Velocity(vx = 0.4, vy = 0.4, vz = 0.4),
            targetDirection = Velocity(vx = -0.4, vy = -0.4, vz = -0.4),
            accelerate = true,
            speedOfLight = 1.0
        )
        println(v3)
    }

    @Test
    fun deltaMassByPhotonRocketTest() {
        val d1 = deltaMassByPhotonRocket(
            initialRestMass = 1.0,
            initialVelocity = Velocity(0.4, 0.4, 0.4),
            targetVelocity = Velocity(0.999, 0.0, 0.0),
            speedOfLight = 1.0
        )
        assert(d1 > 0.97)
    }
}