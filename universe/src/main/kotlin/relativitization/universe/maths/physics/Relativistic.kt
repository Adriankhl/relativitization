package relativitization.universe.maths.physics

import relativitization.universe.data.physics.Velocity
import kotlin.math.sqrt

object Relativistic {
    fun gamma(velocity: Velocity, speedOfLight: Int): Double {
        return 1.0 / sqrt(1.0 - velocity.squareMag() / (speedOfLight * speedOfLight).toDouble())
    }

    /**
     * Compute the relativistic energy of this object
     *
     * @param restMass the rest mass of the object
     * @param velocity the velocity of the object
     * @param speedOfLight speed of light
     *
     *
     * @return energy in actual unit
     */
    fun energy(restMass: Double, velocity: Velocity, speedOfLight: Int): Double {
        return gamma(velocity, speedOfLight) * restMass * (speedOfLight * speedOfLight).toDouble()
    }


    /**
     * Compute the magnitude of the velocity of this object
     *
     * @param restMass the rest mass of the object
     * @param energy relativistic energy of the object in actual unit
     * @param speedOfLight speed of light
     *
     * @return the magnitude of the velocity
     */
    fun energyToVelocityMag(restMass: Double, energy: Double, speedOfLight: Int): Double {
        val gamaInv = restMass * (speedOfLight * speedOfLight).toDouble() / energy
        val v2 = (1.0 - gamaInv * gamaInv) * (speedOfLight * speedOfLight).toDouble()
        return sqrt(v2)
    }

    // E = m * c^2
    // Unit of energy change when c != 1
    // if energy value data is stored as if c=1, scaling is required when c != 1
    // Consider the amount of energy required to accelerate something to 0.5c
    // c = 2 needs 4 times more energy then c = 1, but this the amount of energy
    // should be the same regardless of c, so the energy value should be amplified by 4 times
    fun Double.toActualEnergyUnit(speedOfLight: Int): Double {
        return this * (speedOfLight * speedOfLight).toDouble()
    }

    // E = m * c^2, m = 1 kg, c = 1
    fun Double.toStandardEnergyUnit(speedOfLight: Int): Double {
        return this / (speedOfLight * speedOfLight).toDouble()
    }
}