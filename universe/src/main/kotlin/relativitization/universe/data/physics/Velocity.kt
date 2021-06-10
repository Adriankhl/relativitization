package relativitization.universe.data.physics

import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.sqrt

@Serializable
data class Velocity(val vx: Double, val vy: Double, val vz: Double) {
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

    operator fun times(double: Double) = Velocity(vx * double, vy * double, vz * double)

    operator fun plus(velocity: Velocity) = Velocity(velocity.vx + vx, velocity.vy + vy, velocity.vz + vz)
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
}