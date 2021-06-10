package relativitization.universe.maths.physics

import org.apache.logging.log4j.LogManager
import relativitization.universe.data.physics.Double3D
import relativitization.universe.data.physics.Velocity
import relativitization.universe.maths.physics.Relativistic.decelerateByPhotonRocket
import relativitization.universe.maths.physics.Relativistic.targetVelocityAtDirectionPhotonRocket
import relativitization.universe.maths.physics.Relativistic.targetVelocityByPhotonRocket

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
     * Whether the velocity and double3D is pointing at the same direction
     */
    fun isSameDirection(velocity: Velocity, double3D: Double3D): Boolean {
        val dotProduct: Double = velocity.scaleVelocity(1.0).dot(double3D.normalize())
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
     * Given that the initialVelocity and the double3D pointing from initialPosition to targetPosition are pointing
     * at the same direction, whether the object should decelerate now to stop at target position
     */
    fun shouldDecelerate(
        initialRestMass: Double,
        maxDeltaRestMass: Double,
        initialVelocity: Velocity,
        initialPosition: Double3D,
        targetPosition: Double3D,
        speedOfLight: Double,
    ) {
        val originalDirection: Double3D = (targetPosition - initialPosition).normalize()

        var currentRestMass: Double = initialRestMass
        var currentPosition: Double3D = initialPosition
        var currentDirection: Double3D = (targetPosition - currentPosition).normalize()
        var currentVelocity: Velocity = initialVelocity

        // Maintain this velocity for one turn, then decelerate
        currentPosition += currentVelocity.displacement(1)
        currentDirection = (targetPosition - currentPosition).normalize()

        while (isSameDirection(currentVelocity, currentDirection) && (currentVelocity.squareMag() > 0)) {
            val targetVelocity: Velocity = displacementToVelocity(currentPosition, targetPosition, speedOfLight)

            val requiredDeltaMass: Double = Relativistic.deltaMassByPhotonRocket(
                initialRestMass = currentRestMass,
                initialVelocity = currentVelocity,
                targetVelocity = targetVelocity,
                speedOfLight = speedOfLight,
            )

            if (targetVelocity.mag() > 0.000001) {
                if (requiredDeltaMass <= maxDeltaRestMass) {
                    currentVelocity = targetVelocity
                    currentPosition += currentVelocity.displacement(1)
                    currentRestMass -= requiredDeltaMass
                    currentDirection = (targetPosition - currentPosition).normalize()
                } else {
                    val targetVelocityData: TargetVelocityData = targetVelocityAtDirectionPhotonRocket(
                        initialRestMass = currentRestMass,
                        deltaRestMass = maxDeltaRestMass,
                        initialVelocity = currentVelocity,
                        targetDirection = targetVelocity,
                        accelerate = false,
                        speedOfLight = speedOfLight,
                    )

                    if (targetVelocityData.targetVelocityType == TargetVelocityType.FAIL) {
                        logger.error("Something wrong in shouldDecelerate when computing targetVelocityData")
                    }
                }
            }
        }
    }

    fun targetPositionByPhotonRocket(
        initialRestMass: Double,
        maxDeltaRestMass: Double,
        initialVelocity: Velocity,
        maxSpeed: Double,
        initialPosition: Double3D,
        targetPosition: Double3D,
        speedOfLight: Double,
    ): TargetVelocityData {
        val originalTargetVelocity: Velocity = displacementToVelocity(
            from = initialPosition,
            to = targetPosition,
            speedOfLight = 0.0,
        )

        val targetVelocity: Velocity = if (originalTargetVelocity.mag() > maxSpeed) {
            originalTargetVelocity.scaleVelocity(maxSpeed)
        } else {
            originalTargetVelocity
        }

        val targetVelocityData: TargetVelocityData = targetVelocityByPhotonRocket(
            initialRestMass = initialRestMass,
            maxDeltaRestMass = maxDeltaRestMass,
            initialVelocity = initialVelocity,
            targetVelocity = targetVelocity,
            speedOfLight = 0.0
        )

        return targetVelocityData
    }
}