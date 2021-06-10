package relativitization.universe.maths.physics

import org.apache.logging.log4j.LogManager
import relativitization.universe.data.physics.Double3D
import relativitization.universe.data.physics.Velocity
import relativitization.universe.maths.physics.Intervals.distance
import relativitization.universe.maths.physics.Relativistic.decelerateByPhotonRocket
import relativitization.universe.maths.physics.Relativistic.targetVelocityAtDirectionPhotonRocket
import relativitization.universe.maths.physics.Relativistic.targetVelocityByPhotonRocket
import kotlin.math.min

object Movement {
    private val logger = LogManager.getLogger()

    /**
     * Calculate target velocity by position, moving to that direction and target at 0.9c speed
     *
     * @return target velocity capped at 0.999999 speed of light
     */
    fun displacementToVelocity(from: Double3D, to: Double3D, speedOfLight: Double): Velocity {
        val dx = to.x - from.x
        val dy = to.y - from.y
        val dz = to.z - from.z
        val velocity = Velocity(dx, dy, dz)
        return if (velocity.mag() >= 1.0) {
            velocity.scaleVelocity(speedOfLight * 0.999999)
        } else {
            velocity
        }
    }

    /**
     * Whether the velocity and double3D are pointing at the same direction
     */
    fun isSameDirection(velocity: Velocity, double3D: Double3D): Boolean {
        val dotProduct: Double = velocity.scaleVelocity(1.0).dot(double3D.normalize())
        return (dotProduct > 0.999999) && (dotProduct < 1.000001)
    }


    /**
     * Whether the velocities are pointing at the same direction
     */
    fun isSameDirection(v1: Velocity, v2: Velocity): Boolean {
        val dotProduct: Double = v1.scaleVelocity(1.0).dotUnitVelocity(v2)
        return (dotProduct > 0.999999) && (dotProduct < 1.000001)
    }

    /**
     * Distance needed to stop an object
     */
    fun stoppingDistanceByPhotonRocket(
        initialRestMass: Double,
        maxDeltaRestMass: Double,
        initialVelocity: Velocity,
        speedOfLight: Double,
    ): Double {
        var currentDistance: Double = 0.0
        var currentVelocity: Velocity = initialVelocity
        var currentRestMass: Double = initialRestMass

        while (currentVelocity.squareMag() > 0) {
            val velocityData: TargetVelocityData = decelerateByPhotonRocket(
                initialRestMass = currentRestMass,
                maxDeltaRestMass = maxDeltaRestMass,
                initialVelocity = currentVelocity,
                speedOfLight = speedOfLight
            )

            currentVelocity = velocityData.newVelocity
            currentRestMass -= velocityData.deltaRestMass
            currentDistance += currentVelocity.mag()
        }

        return currentDistance
    }

    /**
     * Fastest speed allowed to stop at a certain distance
     */
    fun maxSpeedToStopByPhotonRocket(
        initialRestMass: Double,
        maxDeltaRestMass: Double,
        distance: Double,
        speedOfLight: Double,
        numIteration: Int = 10,
    ): Double {
        var intervalMin: Double = 0.0
        var intervalMax: Double = speedOfLight

        for (i in 1..numIteration) {
            val testSpeed: Double = 0.5 * intervalMin + 0.5 * intervalMax
            val stoppingDistance: Double = stoppingDistanceByPhotonRocket(
                initialRestMass = initialRestMass,
                maxDeltaRestMass = maxDeltaRestMass,
                initialVelocity = Velocity(testSpeed, 0.0, 0.0),
                speedOfLight = speedOfLight
            )

            if (stoppingDistance < distance) {
                intervalMin = testSpeed
            } else {
                intervalMax = testSpeed
            }
        }

        return intervalMin
    }


    fun targetDouble3DByPhotonRocket(
        initialRestMass: Double,
        maxDeltaRestMass: Double,
        initialVelocity: Velocity,
        maxSpeed: Double,
        initialDouble3D: Double3D,
        targetDouble3D: Double3D,
        speedOfLight: Double,
    ): TargetVelocityData {

        val distance: Double = distance(initialDouble3D, targetDouble3D)
        val originalTargetVelocity: Velocity = displacementToVelocity(initialDouble3D, targetDouble3D, speedOfLight)
        val maxSpeedByDistance: Double = min(maxSpeed, maxSpeedToStopByPhotonRocket(
            initialRestMass = initialRestMass,
            maxDeltaRestMass = maxDeltaRestMass,
            distance = distance,
            speedOfLight = speedOfLight,
        ))

        val targetVelocity: Velocity = if (originalTargetVelocity.mag() > maxSpeedByDistance) {
            originalTargetVelocity.scaleVelocity(maxSpeedByDistance)
        } else {
            originalTargetVelocity
        }

        val requiredDeltaMass: Double = Relativistic.deltaMassByPhotonRocket(
            initialRestMass = initialRestMass,
            initialVelocity = initialVelocity,
            targetVelocity = targetVelocity,
            speedOfLight = speedOfLight,
        )

        return if (initialDouble3D == targetDouble3D) {
            if (requiredDeltaMass <= maxDeltaRestMass && (targetVelocity == originalTargetVelocity)) {
                TargetVelocityData(
                    TargetVelocityType.SUCCESS,
                    targetVelocity,
                    requiredDeltaMass
                )
            } else {
                decelerateByPhotonRocket(
                    initialRestMass = initialRestMass,
                    maxDeltaRestMass = maxDeltaRestMass,
                    initialVelocity = initialVelocity,
                    speedOfLight = speedOfLight
                )
            }
        } else {

            if ((requiredDeltaMass <= maxDeltaRestMass) && (targetVelocity == originalTargetVelocity)) {
                TargetVelocityData(
                    TargetVelocityType.SUCCESS,
                    targetVelocity,
                    requiredDeltaMass
                )
            } else {
                targetVelocityByPhotonRocket(
                    initialRestMass = initialRestMass,
                    maxDeltaRestMass = maxDeltaRestMass,
                    initialVelocity = initialVelocity,
                    targetVelocity = targetVelocity,
                    speedOfLight = speedOfLight
                )
            }
        }
    }
}