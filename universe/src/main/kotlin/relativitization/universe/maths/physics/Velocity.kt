package relativitization.universe.maths.physics

import kotlinx.serialization.Serializable
import kotlin.math.*
import kotlin.random.Random

@Serializable
data class Velocity(val vx: Double, val vy: Double, val vz: Double) {
    operator fun times(double: Double) = Velocity(vx * double, vy * double, vz * double)

    operator fun plus(velocity: Velocity) =
        Velocity(vx + velocity.vx, vy + velocity.vy, vz + velocity.vz)

    operator fun minus(velocity: Velocity) =
        Velocity(vx - velocity.vx, vy - velocity.vy, vz - velocity.vz)

    fun squareMag(): Double {
        return vx * vx + vy * vy + vz * vz
    }

    fun mag(): Double = sqrt(squareMag())

    fun scaleVelocity(newVMag: Double): Velocity {
        val vMag = mag()
        return if (vMag <= 0.0) {
            Velocity(0.0, 0.0, 0.0)
        } else {
            val ratio = newVMag / vMag
            Velocity(ratio * vx, ratio * vy, ratio * vz)
        }
    }

    fun toMutableVelocity() = MutableVelocity(vx, vy, vz)

    /**
     * Find the maximum component of the velocity
     *
     * @return a pair of the component name and the absolute value of the component
     */
    fun maxComponent(): Pair<Char, Double> {
        val absX: Double = abs(vx)
        val absY: Double = abs(vy)
        val absZ: Double = abs(vz)

        return if ((absX > absY) && (absX > absZ)) {
            Pair('x', absX)
        } else if ((absY > absX) && (absY > absZ)) {
            Pair('y', absY)
        } else {
            Pair('z', absZ)
        }
    }

    fun dot(velocity: Velocity): Double {
        return vx * velocity.vx + vy * velocity.vy + vz * velocity.vz
    }

    fun dot(double3D: Double3D): Double {
        return vx * double3D.x + vy * double3D.y + vz * double3D.z
    }

    fun dotUnitVelocity(velocity: Velocity): Double {
        val unitVelocity: Velocity = velocity.scaleVelocity(1.0)
        return dot(unitVelocity)
    }

    fun displacement(time: Int): Double3D {
        return Double3D(vx * time, vy * time, vz * time)
    }

    fun cross(velocity: Velocity) = Velocity(
        vy * velocity.vz - vz * velocity.vy,
        vz * velocity.vx - vx * velocity.vz,
        vx * velocity.vy - vy * velocity.vx,
    )

    /**
     * Compute a pair of perpendicular unit vectors, form a new coordinate system
     *
     * @return (vector of x-axis, vector of y-axis) where the original vector is the z axis
     */
    fun perpendicularUnitVectorPair(): Pair<Velocity, Velocity> {
        val originalUnitVector: Velocity = scaleVelocity(1.0)

        val referenceVector: Velocity = if (
            (originalUnitVector.vx != 0.0) ||
            (originalUnitVector.vy != 0.0)
        ) {
            Velocity(0.0, 0.0, 1.0)
        } else {
            Velocity(1.0, 0.0, 0.0)
        }

        val vector1: Velocity = originalUnitVector.cross(referenceVector).scaleVelocity(1.0)
        val vector2: Velocity = originalUnitVector.cross(vector1).scaleVelocity(1.0)

        return Pair(vector1, vector2)
    }

    /**
     * Randomly rotate the vector with a limit of max theta to rotate, in a spherical coordinate system where the
     * original vector is the z axis
     * https://www.bogotobogo.com/Algorithms/uniform_distribution_sphere.php
     */
    fun randomRotate(
        maxRotateTheta: Double,
        random: Random,
    ): Velocity {
        return if (maxRotateTheta > 0.0) {
            val originalMag: Double = mag()

            val axisPair: Pair<Velocity, Velocity> = perpendicularUnitVectorPair()
            val xAxis: Velocity = axisPair.first
            val yAxis: Velocity = axisPair.second
            val zAxis: Velocity = scaleVelocity(1.0)

            // the angular coordinate where the original vector is the z-axis
            val phi: Double = 2.0 * PI * random.nextDouble()
            // Bound the uniform random number for theta
            val minRand: Double = (cos(maxRotateTheta) + 1.0) * 0.5
            val maxRand: Double = 1.0
            val theta: Double = acos(2.0 * random.nextDouble(minRand, maxRand) - 1.0)

            val newUnitVector: Velocity =
                xAxis * cos(phi) * sin(theta) + yAxis * sin(phi) * sin(theta) + zAxis * cos(theta)

            newUnitVector * originalMag
        } else {
            this
        }
    }
}

@Serializable
data class MutableVelocity(var vx: Double, var vy: Double, var vz: Double) {
    fun squareMag(): Double {
        return vx * vx + vy * vy + vz * vz
    }

    fun mag(): Double = sqrt(squareMag())

    fun scaleVelocity(newVMag: Double): MutableVelocity {
        val vMag = sqrt(squareMag())
        val ratio = newVMag / vMag
        return MutableVelocity(ratio * vx, ratio * vy, ratio * vz)
    }

    fun toVelocity(): Velocity = Velocity(vx, vy, vz)
}