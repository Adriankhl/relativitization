package relativitization.universe.maths.physics

import relativitization.universe.data.components.default.physics.Double3D
import relativitization.universe.data.components.default.physics.Velocity
import relativitization.universe.maths.physics.Intervals.distance
import relativitization.universe.maths.physics.Relativistic.decelerateByPhotonRocket
import relativitization.universe.maths.physics.Relativistic.deltaMassByPhotonRocket
import relativitization.universe.maths.physics.Relativistic.targetVelocityByPhotonRocket
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.min

object Movement {
    private val logger = RelativitizationLogManager.getLogger()

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
     * Given a initial speed, the distance needed to stop the object by photon rocket
     *
     * @param initialRestMass initial rest mass of the object
     * @param maxDeltaRestMass maximum change of rest mass to photon to stop the object
     * @param initialVelocity initial velocity
     * @param speedOfLight speed of light
     * @param numIteration maximum number of iteration
     */
    fun stoppingDistanceByPhotonRocket(
        initialRestMass: Double,
        maxDeltaRestMass: Double,
        initialVelocity: Velocity,
        speedOfLight: Double,
        numIteration: Int = 100,
    ): Double {
        var currentDistance: Double = 0.0
        var currentVelocity: Velocity = initialVelocity
        var currentRestMass: Double = initialRestMass

        loop@ for (i in 1..numIteration) {
            if (currentVelocity.squareMag() > 0) {
                val velocityData: TargetVelocityData = decelerateByPhotonRocket(
                    initialRestMass = currentRestMass,
                    maxDeltaRestMass = maxDeltaRestMass,
                    initialVelocity = currentVelocity,
                    speedOfLight = speedOfLight
                )

                currentVelocity = velocityData.newVelocity
                currentRestMass -= velocityData.deltaRestMass
                currentDistance += currentVelocity.mag()
            } else {
                break@loop
            }
        }

        return if (currentVelocity.squareMag() > 0) {
            Double.POSITIVE_INFINITY
        } else {
            currentDistance
        }
    }

    /**
     * Requiring the object to stop at a certain distance, compute the maximum initial speed
     *
     * @param initialRestMass initial rest mass of the object
     * @param maxDeltaRestMass maximum change of rest mass to photon to stop the object
     * @param distance the object should stop within this distance
     * @param speedOfLight speed of light
     * @param numIteration number of iteration to compute the speed, more iterations gives a more accurate answer
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


    /**
     * Compute the velocity the object should reached to move to a target double3D position
     *
     * @param initialRestMass initial rest mass of the object
     * @param maxDeltaRestMass maximum change of rest mass to photon to stop the object
     * @param initialVelocity initial velocity
     * @param maxSpeed the maximum speed limit of the object, to prevent using too much rest mass as fuel
     * @param initialDouble3D initial double3D position
     * @param targetDouble3D target double3D position
     * @param speedOfLight speed of light
     */
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
        val originalTargetVelocity: Velocity =
            displacementToVelocity(initialDouble3D, targetDouble3D, speedOfLight)
        val maxSpeedByDistance: Double = min(
            maxSpeed, maxSpeedToStopByPhotonRocket(
                initialRestMass = initialRestMass,
                maxDeltaRestMass = maxDeltaRestMass,
                distance = distance,
                speedOfLight = speedOfLight,
            )
        )

        val targetVelocity: Velocity = if (originalTargetVelocity.mag() > maxSpeedByDistance) {
            originalTargetVelocity.scaleVelocity(maxSpeedByDistance)
        } else {
            originalTargetVelocity
        }

        val requiredDeltaMass: Double = deltaMassByPhotonRocket(
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

    /**
     * Estimate the required mass to move to the target position, not full accurate
     *
     * @param initialRestMass initial rest mass of the object
     * @param initialVelocity initial velocity
     * @param maxSpeed the maximum speed limit of the object, to prevent using too much rest mass as fuel
     * @param initialDouble3D initial double3D position
     * @param targetDouble3D target double3D position
     * @param speedOfLight speed of light
     */
    fun estimateRequiredDeltaMass(
        initialRestMass: Double,
        initialVelocity: Velocity,
        maxSpeed: Double,
        initialDouble3D: Double3D,
        targetDouble3D: Double3D,
        speedOfLight: Double,
    ): Double {

        val maxVelocity: Velocity =
            displacementToVelocity(initialDouble3D, targetDouble3D, speedOfLight).scaleVelocity(
                maxSpeed
            )
        val requiredDeltaMass1: Double = deltaMassByPhotonRocket(
            initialRestMass = initialRestMass,
            initialVelocity = initialVelocity,
            targetVelocity = maxVelocity,
            speedOfLight = speedOfLight,
        )

        val requiredDeltaMass2: Double = deltaMassByPhotonRocket(
            initialRestMass = initialRestMass - requiredDeltaMass1,
            initialVelocity = maxVelocity,
            targetVelocity = Velocity(0.0, 0.0, 0.0),
            speedOfLight = speedOfLight,
        )

        return requiredDeltaMass1 + requiredDeltaMass2
    }

    /**
     * Run several iteration to move the object by photon rocket
     *
     * @param initialRestMass initial rest mass of the object
     * @param maxDeltaRestMass maximum change of rest mass to photon to stop the object
     * @param initialVelocity initial velocity
     * @param maxSpeed the maximum speed limit of the object, to prevent using too much rest mass as fuel
     * @param initialDouble3D initial double3D position
     * @param targetDouble3D target double3D position
     * @param speedOfLight speed of light
     * @param numIteration number of turn to move the object
     */
    fun runTargetDouble3DByPhotonRocket(
        initialRestMass: Double,
        maxDeltaRestMass: Double,
        initialVelocity: Velocity,
        maxSpeed: Double,
        initialDouble3D: Double3D,
        targetDouble3D: Double3D,
        speedOfLight: Double,
        numIteration: Int,
    ): MovementStateData {

        var currentRestMass = initialRestMass
        var currentDouble3D = initialDouble3D
        var currentVelocity = initialVelocity

        loop@ for (i in 1..numIteration) {
            if (currentDouble3D != targetDouble3D) {
                val targetVelocityData: TargetVelocityData = targetDouble3DByPhotonRocket(
                    initialRestMass = currentRestMass,
                    maxDeltaRestMass = maxDeltaRestMass,
                    initialVelocity = currentVelocity,
                    maxSpeed = maxSpeed,
                    initialDouble3D = currentDouble3D,
                    targetDouble3D = targetDouble3D,
                    speedOfLight = speedOfLight
                )

                currentRestMass -= targetVelocityData.deltaRestMass
                currentVelocity = targetVelocityData.newVelocity
                currentDouble3D += targetVelocityData.newVelocity.displacement(1)
            } else {
                break@loop
            }
        }

        return MovementStateData(
            currentRestMass,
            currentVelocity,
            currentDouble3D
        )
    }


    /**
     * Upper bound of the required delta mass to move to the target position, given that the
     * rest mass does not increase
     *
     * @param initialRestMass initial rest mass of the object
     * @param maxDeltaRestMass maximum change of rest mass to photon to stop the object
     * @param initialVelocity initial velocity
     * @param maxSpeed the maximum speed limit of the object, to prevent using too much rest mass as fuel
     * @param initialDouble3D initial double3D position
     * @param targetDouble3D target double3D position
     * @param speedOfLight speed of light
     */
    fun requiredDeltaRestMassUpperBound(
        initialRestMass: Double,
        maxDeltaRestMass: Double,
        initialVelocity: Velocity,
        maxSpeed: Double,
        initialDouble3D: Double3D,
        targetDouble3D: Double3D,
        speedOfLight: Double,
        numIteration: Int = 100,
    ): Double {
        // Test the movement by 100 iteration
        val testMovementFinalState: MovementStateData = runTargetDouble3DByPhotonRocket(
            initialRestMass = initialRestMass,
            maxDeltaRestMass = maxDeltaRestMass,
            initialVelocity = initialVelocity,
            maxSpeed = maxSpeed,
            initialDouble3D = initialDouble3D,
            targetDouble3D = targetDouble3D,
            speedOfLight = speedOfLight,
            numIteration = numIteration,
        )

        return if ((testMovementFinalState.double3D == targetDouble3D) && (
                    testMovementFinalState.velocity.squareMag() == 0.0)
        ) {
            initialRestMass - testMovementFinalState.restMass
        } else {
            // First stop to zero velocity, then accelerate to max speed, then stop to zero again
            val maxVelocity: Velocity =
                displacementToVelocity(initialDouble3D, targetDouble3D, speedOfLight).scaleVelocity(
                    maxSpeed
                )
            val requiredDeltaMass1: Double = deltaMassByPhotonRocket(
                initialRestMass = initialRestMass,
                initialVelocity = initialVelocity,
                targetVelocity = Velocity(0.0, 0.0, 0.0),
                speedOfLight = speedOfLight,
            )

            val requiredDeltaMass2: Double = deltaMassByPhotonRocket(
                initialRestMass = initialRestMass - requiredDeltaMass1,
                initialVelocity = Velocity(0.0, 0.0, 0.0),
                targetVelocity = maxVelocity,
                speedOfLight = speedOfLight,
            )

            val requiredDeltaMass3: Double = deltaMassByPhotonRocket(
                initialRestMass = initialRestMass - requiredDeltaMass1 - requiredDeltaMass2,
                initialVelocity = maxVelocity,
                targetVelocity = Velocity(0.0, 0.0, 0.0),
                speedOfLight = speedOfLight,
            )

            requiredDeltaMass1 + requiredDeltaMass2 + requiredDeltaMass3
        }
    }

    /**
     * Compute the overall delta mass required to move to a target double3D position
     *
     * @param initialRestMass initial rest mass of the object
     * @param maxDeltaRestMass maximum change of rest mass to photon to stop the object
     * @param initialVelocity initial velocity
     * @param maxSpeed the maximum speed limit of the object, to prevent using too much rest mass as fuel
     * @param initialDouble3D initial double3D position
     * @param targetDouble3D target double3D position
     * @param speedOfLight speed of light
     */
    fun deltaMassTargetDouble3DByPhotonRocket(
        initialRestMass: Double,
        maxDeltaRestMass: Double,
        initialVelocity: Velocity,
        maxSpeed: Double,
        initialDouble3D: Double3D,
        targetDouble3D: Double3D,
        speedOfLight: Double,
    ): Double {

        val currentMovementState: MovementStateData = runTargetDouble3DByPhotonRocket(
            initialRestMass = initialRestMass,
            maxDeltaRestMass = maxDeltaRestMass,
            initialVelocity = initialVelocity,
            maxSpeed = maxSpeed,
            initialDouble3D = initialDouble3D,
            targetDouble3D = targetDouble3D,
            speedOfLight = speedOfLight,
            numIteration = Int.MAX_VALUE,

            )
        return initialRestMass - currentMovementState.restMass
    }
}

data class MovementStateData(
    val restMass: Double,
    val velocity: Velocity,
    val double3D: Double3D,
)