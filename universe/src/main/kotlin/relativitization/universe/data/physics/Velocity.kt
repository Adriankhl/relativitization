package relativitization.universe.data.physics

import kotlinx.serialization.Serializable
import kotlin.math.sqrt

@Serializable
data class Velocity(var vx: Double, var vy: Double, var vz: Double) {
    fun squareMag(): Double {
        return vx * vx + vy * vy + vz * vz
    }

    fun scaleVelocity(newVMag: Double): Velocity {
        val vMag = sqrt(squareMag())
        val ratio = newVMag / vMag
        return Velocity(ratio * vx, ratio * vy, ratio * vz)
    }
}