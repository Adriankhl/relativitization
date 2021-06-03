package relativitization.universe.data.physics

import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.sqrt

@Serializable
data class Velocity(val vx: Double, val vy: Double, val vz: Double) {
    fun squareMag(): Double {
        return vx * vx + vy * vy + vz * vz
    }

    fun scaleVelocity(newVMag: Double): Velocity {
        val vMag = sqrt(squareMag())
        return if (vMag <= 0.0) {
            Velocity(0.0, 0.0, 0.0)
        } else {
            val ratio = newVMag / vMag
            Velocity(ratio * vx, ratio * vy, ratio * vz)
        }
    }

    fun maxComponent(): Pair<Char, Double> {
        val absX: Double = abs(vx)
        val absY: Double = abs(vy)
        val absZ: Double = abs(vz)

        return if ((absX > absY) && (absX > absZ)) {
            Pair('x', vx)
        } else if ((absY > absX) && (absY > absZ)) {
            Pair('y', vy)
        } else {
            Pair('z', vz)
        }
    }

    operator fun times(double: Double) = Velocity(vx * double, vy * double, vz * double)

    operator fun plus(velocity: Velocity) = Velocity(velocity.vx + vx, velocity.vy + vy, velocity.vz + vz)
}

@Serializable
data class MutableVelocity(var vx: Double, var vy: Double, var vz: Double) {
    fun squareMag(): Double {
        return vx * vx + vy * vy + vz * vz
    }

    fun scaleVelocity(newVMag: Double): MutableVelocity {
        val vMag = sqrt(squareMag())
        val ratio = newVMag / vMag
        return MutableVelocity(ratio * vx, ratio * vy, ratio * vz)
    }
}