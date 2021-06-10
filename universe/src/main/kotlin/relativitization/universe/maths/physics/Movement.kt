package relativitization.universe.maths.physics

import relativitization.universe.data.physics.Double3D
import relativitization.universe.data.physics.Velocity
import relativitization.universe.maths.physics.Relativistic.targetVelocityByPhotonRocket

object Movement {
    /**
     * Calculate target velocity by position, moving to that direction and target at 0.9c speed
     *
     * @return target velocity at 0.9 speed of light
     */ fun displacementToVelocity(from: Double3D, to: Double3D, speedOfLight: Double): Velocity {
        val dx = to.x - from.x
        val dy = to.y - from.y
        val dz = to.z - from.z
        return Velocity(dx, dy, dz).scaleVelocity(speedOfLight * 0.9)
    }

    fun shouldDecelerate(
        initialRestMass: Double,
        maxDeltaRestMass: Double,
        initialVelocity: Velocity,
        initialPosition: Double3D,
        targetPosition: Double3D,
        speedOfLight: Double,
    ) {

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
    }
}