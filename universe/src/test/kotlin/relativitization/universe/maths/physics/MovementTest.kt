package relativitization.universe.maths.physics

import relativitization.universe.data.components.defaults.physics.Double3D
import relativitization.universe.data.components.defaults.physics.Velocity
import relativitization.universe.maths.physics.Movement.deltaMassTargetDouble3DByPhotonRocket
import relativitization.universe.maths.physics.Movement.estimateRequiredDeltaMass
import relativitization.universe.maths.physics.Movement.isSameDirection
import relativitization.universe.maths.physics.Movement.maxSpeedToStopByPhotonRocket
import relativitization.universe.maths.physics.Movement.requiredDeltaRestMassSimpleEstimation
import relativitization.universe.maths.physics.Movement.stoppingDistanceByPhotonRocket
import relativitization.universe.maths.physics.Movement.targetDouble3DByPhotonRocket
import relativitization.universe.maths.physics.Relativistic.targetVelocityByPhotonRocket
import kotlin.math.abs
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

    @Test
    fun maxSpeedTest() {
        val d1: Double = maxSpeedToStopByPhotonRocket(
            initialRestMass = 1.0,
            maxDeltaRestMass = 0.1,
            distance = 2.0,
            speedOfLight = 1.0
        )

        assert(d1 > 0.67 && d1 < 0.68)
    }

    @Test
    fun targetDouble3DTest() {

        var currentRestMass1 = 1.0
        var currentDouble3D1 = Double3D(0.0, 0.0, 0.0)
        var currentVelocity1 = Velocity(0.0, 0.0, 0.0)

        val l1 = (1..10).toList().map {
            val v1: TargetVelocityData = targetDouble3DByPhotonRocket(
                initialRestMass = currentRestMass1,
                maxDeltaRestMass = 0.1,
                initialVelocity = currentVelocity1,
                maxSpeed = 0.2,
                initialDouble3D = currentDouble3D1,
                targetDouble3D = Double3D(1.0, 0.0, 0.0),
                speedOfLight = 1.0
            )
            currentRestMass1 -= v1.deltaRestMass
            currentVelocity1 = v1.newVelocity
            currentDouble3D1 += v1.newVelocity.displacement(1)

            println("Velocity 1: $currentVelocity1")
            println("Double3D 1: $currentDouble3D1")
            println("Rest mass 1: $currentRestMass1")
        }

        println(l1)

        assert(currentDouble3D1 == Double3D(1.0, 0.0, 0.0))

        var currentRestMass2 = 1.0
        var currentDouble3D2 = Double3D(0.0, 0.0, 0.0)
        var currentVelocity2 = Velocity(0.0, -0.3, 0.0)

        val l2 = (1..10).toList().map {
            val v2: TargetVelocityData = targetDouble3DByPhotonRocket(
                initialRestMass = currentRestMass2,
                maxDeltaRestMass = 0.1,
                initialVelocity = currentVelocity2,
                maxSpeed = 0.2,
                initialDouble3D = currentDouble3D2,
                targetDouble3D = Double3D(1.0, 0.0, 0.0),
                speedOfLight = 1.0
            )
            currentRestMass2 -= v2.deltaRestMass
            currentVelocity2 = v2.newVelocity
            currentDouble3D2 += v2.newVelocity.displacement(1)

            println("Velocity 2: $currentVelocity2")
            println("Double3D 2: $currentDouble3D2")
            println("Rest mass 2: $currentRestMass2")
        }

        println(l2)

        assert(currentDouble3D2 == Double3D(1.0, 0.0, 0.0))

        var currentRestMass3 = 1.0
        var currentDouble3D3 = Double3D(0.0, 0.0, 0.0)
        var currentVelocity3 = Velocity(0.5, 0.0, 0.0)

        val l6 = (1..30).toList().map {
            val v3: TargetVelocityData = targetDouble3DByPhotonRocket(
                initialRestMass = currentRestMass3,
                maxDeltaRestMass = 0.05,
                initialVelocity = currentVelocity3,
                maxSpeed = 0.2,
                initialDouble3D = currentDouble3D3,
                targetDouble3D = Double3D(1.0, 0.0, 0.0),
                speedOfLight = 1.0
            )
            currentRestMass3 -= v3.deltaRestMass
            currentVelocity3 = v3.newVelocity
            currentDouble3D3 += v3.newVelocity.displacement(1)

            println("Velocity 3: $currentVelocity3")
            println("Double3D 3: $currentDouble3D3")
            println("Rest mass 3: $currentRestMass3")
        }

        println(l6)

        assert(currentDouble3D3 == Double3D(1.0, 0.0, 0.0))

        var currentRestMass4 = 1.0
        var currentDouble3D4 = Double3D(1.0, 0.0, 0.0)
        var currentVelocity4 = Velocity(0.6, 0.0, 0.0)

        val l4 = (1..30).toList().map {
            val v4: TargetVelocityData = targetDouble3DByPhotonRocket(
                initialRestMass = currentRestMass4,
                maxDeltaRestMass = 0.1,
                initialVelocity = currentVelocity4,
                maxSpeed = 0.2,
                initialDouble3D = currentDouble3D4,
                targetDouble3D = Double3D(1.0, 0.0, 0.0),
                speedOfLight = 1.0
            )
            currentRestMass4 -= v4.deltaRestMass
            currentVelocity4 = v4.newVelocity
            currentDouble3D4 += v4.newVelocity.displacement(1)

            println("Velocity 4: $currentVelocity4")
            println("Double3D 4: $currentDouble3D4")
            println("Rest mass 4: $currentRestMass4")
        }

        println(l4)

        assert(currentDouble3D4 == Double3D(1.0, 0.0, 0.0))

    }

    @Test
    fun estimateRequiredDeltaMassTest() {
        var currentRestMass1 = 1.0
        var currentDouble3D1 = Double3D(0.0, 0.0, 0.0)
        var currentVelocity1 = Velocity(0.0, 0.0, 0.0)

        val l1 = (1..10).toList().map {
            val v1: TargetVelocityData = targetDouble3DByPhotonRocket(
                initialRestMass = currentRestMass1,
                maxDeltaRestMass = 0.1,
                initialVelocity = currentVelocity1,
                maxSpeed = 0.2,
                initialDouble3D = currentDouble3D1,
                targetDouble3D = Double3D(1.0, 0.0, 0.0),
                speedOfLight = 1.0
            )
            currentRestMass1 -= v1.deltaRestMass
            currentVelocity1 = v1.newVelocity
            currentDouble3D1 += v1.newVelocity.displacement(1)

            println("Velocity 1: $currentVelocity1")
            println("Double3D 1: $currentDouble3D1")
            println("Rest mass 1: $currentRestMass1")
        }

        println(l1)

        val e1: Double = requiredDeltaRestMassSimpleEstimation(
            initialRestMass = 1.0,
            initialVelocity = Velocity(0.0, 0.0, 0.0),
            maxSpeed = 0.2,
            speedOfLight = 1.0
        )

        assert(abs(e1 - (1.0 - currentRestMass1)) < 0.001)


        var currentRestMass2 = 1.0
        var currentDouble3D2 = Double3D(0.0, 0.0, 0.0)
        var currentVelocity2 = Velocity(0.0, -0.3, 0.0)

        val l2 = (1..10).toList().map {
            val v2: TargetVelocityData = targetDouble3DByPhotonRocket(
                initialRestMass = currentRestMass2,
                maxDeltaRestMass = 0.1,
                initialVelocity = currentVelocity2,
                maxSpeed = 0.2,
                initialDouble3D = currentDouble3D2,
                targetDouble3D = Double3D(1.0, 0.0, 0.0),
                speedOfLight = 1.0
            )
            currentRestMass2 -= v2.deltaRestMass
            currentVelocity2 = v2.newVelocity
            currentDouble3D2 += v2.newVelocity.displacement(1)

            println("Velocity 2: $currentVelocity2")
            println("Double3D 2: $currentDouble3D2")
            println("Rest mass 2: $currentRestMass2")
        }

        println(l2)

        val e2: Double = requiredDeltaRestMassSimpleEstimation(
            initialRestMass = 1.0,
            initialVelocity = Velocity(0.0, -0.3, 0.0),
            maxSpeed = 0.2,
            speedOfLight = 1.0
        )

        assert(abs(e2 - (1.0 - currentRestMass2)) < 0.1)
    }

    @Test
    fun deltaMassTest() {
        val d1: Double = deltaMassTargetDouble3DByPhotonRocket(
            initialRestMass = 1.0,
            maxDeltaRestMass = 0.1,
            initialVelocity = Velocity(0.0, 0.0, 0.0),
            maxSpeed = 0.2,
            initialDouble3D = Double3D(0.0, 0.0, 0.0),
            targetDouble3D = Double3D(1.0, 0.0, 0.0),
            speedOfLight = 1.0
        )
        assert(d1 > 0.26 && d1 < 0.27)

        val d2: Double = deltaMassTargetDouble3DByPhotonRocket(
            initialRestMass = 1.0,
            maxDeltaRestMass = 0.1,
            initialVelocity = Velocity(0.4, 0.2, -0.1),
            maxSpeed = 0.2,
            initialDouble3D = Double3D(0.0, 0.0, 0.0),
            targetDouble3D = Double3D(-0.23, 0.12, 0.45),
            speedOfLight = 1.0
        )

        assert(d2 > 0.53 && d2 < 0.54)
    }
}