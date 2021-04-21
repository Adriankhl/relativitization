package relativitization.universe.data.physics

import kotlinx.serialization.Serializable
import kotlin.math.sqrt

@Serializable
data class Velocity(val vx: Double, val vy: Double, val vz: Double) {
    fun squareMag(): Double {
        return vx * vx + vy * vy + vz * vz
    }

    fun scaleVelocity(newVMag: Double): Velocity {
        val vMag = sqrt(squareMag())
        val ratio = newVMag / vMag
        return Velocity(ratio * vx, ratio * vy, ratio * vz)
    }
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