package relativitization.universe.maths.physics

import relativitization.universe.data.physics.Velocity
import kotlin.math.sqrt

object Relativistic {
    fun gamma(velocity: Velocity, speedOfLight: Int): Double {
        return 1.0 / sqrt(1.0 - velocity.squareMag() / (speedOfLight * speedOfLight).toDouble())
    }

    fun energy(restMass: Double, velocity: Velocity, speedOfLight: Int): Double {
        return gamma(velocity, speedOfLight) * restMass * (speedOfLight * speedOfLight).toDouble()
    }

    fun energyToVelocityMag(restMass: Double, energy: Double, speedOfLight: Int): Double {
        val gamaInv = restMass * (speedOfLight * speedOfLight).toDouble() / energy
        val v2 = (1.0 - gamaInv * gamaInv) * (speedOfLight * speedOfLight).toDouble()
        return sqrt(v2)
    }
}