package relativitization.universe.maths.physics

import org.junit.jupiter.api.TestFactory
import relativitization.universe.data.commands.ChangeVelocityCommand
import relativitization.universe.data.physics.Velocity
import relativitization.universe.maths.physics.Relativistic.canTargetVelocityAtDirectionByPhotonRocket
import relativitization.universe.maths.physics.Relativistic.decelerateByPhotonRocket
import relativitization.universe.maths.physics.Relativistic.deltaMassByPhotonRocket
import relativitization.universe.maths.physics.Relativistic.speedByPhotonRocket
import relativitization.universe.maths.physics.Relativistic.targetVelocityAtDirectionPhotonRocket
import kotlin.test.Test

internal class RelativisticTest {
    @Test
    fun canTargetVelocityAtDirectionTest() {
        val b1: Boolean = canTargetVelocityAtDirectionByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.1,
            initialVelocity = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            targetDirection = Velocity(vx = 0.4, vy = 0.4, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(b1)

        val b2: Boolean = canTargetVelocityAtDirectionByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.1,
            initialVelocity = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            targetDirection = Velocity(vx = 0.4, vy = -0.4, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(!b2)

        val b3: Boolean = canTargetVelocityAtDirectionByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.1,
            initialVelocity = Velocity(vx = 0.0, vy = 0.0, vz = 0.0),
            targetDirection = Velocity(vx = 0.4, vy = -0.4, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(b3)

        val b4: Boolean = canTargetVelocityAtDirectionByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.5,
            initialVelocity = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            targetDirection = Velocity(vx = 0.4, vy = -0.4, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(b4)

        val b5: Boolean = canTargetVelocityAtDirectionByPhotonRocket(
            initialRestMass = 1.0,
            deltaRestMass = 0.1,
            initialVelocity = Velocity(vx = 0.3, vy = 0.3, vz = 0.3),
            targetDirection = Velocity(vx = 0.4, vy = 0.3, vz = 0.4),
            speedOfLight = 1.0
        )

        assert(b5)
    }

    @Test
    fun targetVelocityAtDirectionTest() {
        val v1: VelocityChangeData = targetVelocityAtDirectionPhotonRocket(
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

        assert(v1.newVelocity.mag() - s1 < 0.01)

        val v2: VelocityChangeData = targetVelocityAtDirectionPhotonRocket(
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

        assert(v2.newVelocity.mag() - s2 < 0.01)

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

    @Test
    fun decelerateTest() {

        val speedList = listOf(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9)

        speedList.forEach {
            val v1: VelocityChangeData = decelerateByPhotonRocket(
                initialRestMass = 1.0,
                maxDeltaRestMass = 0.1,
                initialVelocity = Velocity(vx = 0.4, vy = 0.4, vz = 0.4).scaleVelocity(it),
                speedOfLight = 1.0
            )

            assert(v1.success)
        }
    }
}