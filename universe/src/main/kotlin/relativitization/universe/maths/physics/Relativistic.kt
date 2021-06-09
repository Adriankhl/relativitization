package relativitization.universe.maths.physics

import relativitization.universe.data.physics.Velocity
import relativitization.universe.maths.algebra.Quadratic.discriminant
import kotlin.math.pow
import kotlin.math.sqrt

object Relativistic {
    // E = m * c^2
    // Unit of energy change when c != 1
    // if energy value data is stored as if c=1, scaling is required when c != 1
    // Consider the amount of energy required to accelerate something to 0.5c
    // c = 2 needs 4 times more energy then c = 1, but this the amount of energy
    // should be the same regardless of c, so the energy value should be amplified by 4 times
    fun Double.toActualEnergyUnit(speedOfLight: Double): Double {
        return this * (speedOfLight * speedOfLight)
    }

    // E = m * c^2, m = 1 kg, c = 1
    fun Double.toStandardEnergyUnit(speedOfLight: Double): Double {
        return this / (speedOfLight * speedOfLight)
    }

    fun gamma(speed: Double, speedOfLight: Double): Double {
        return 1.0 / sqrt(1.0 - speed * speed / speedOfLight / speedOfLight)
    }

    fun gamma(velocity: Velocity, speedOfLight: Double): Double {
        return 1.0 / sqrt(1.0 - velocity.squareMag() / speedOfLight / speedOfLight)
    }

    /**
     * Compute the relativistic energy of this object
     *
     * @param restMass the rest mass of the object
     * @param speed the velocity of the object
     * @param speedOfLight speed of light
     *
     *
     * @return energy in actual unit
     */
    fun speedToEnergy(restMass: Double, speed: Double, speedOfLight: Double): Double {
        return gamma(speed, speedOfLight) * restMass * (speedOfLight * speedOfLight)
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
    fun velocityToEnergy(restMass: Double, velocity: Velocity, speedOfLight: Double): Double {
        return gamma(velocity, speedOfLight) * restMass * (speedOfLight * speedOfLight)
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
    fun energyToSpeed(restMass: Double, energy: Double, speedOfLight: Double): Double {
        val gamaInv = restMass * (speedOfLight * speedOfLight) / energy
        val v2 = (1.0 - gamaInv * gamaInv) * (speedOfLight * speedOfLight)
        return sqrt(v2)
    }

    fun canChangeVelocityByPhotonRocket(
        initialRestMass: Double,
        deltaRestMass: Double,
        initialVelocity: Velocity,
        targetDirection: Velocity,
        speedOfLight: Double,
    ): Boolean {
        val finalRestMass: Double = initialRestMass - deltaRestMass
        val initialGamma: Double = gamma(initialVelocity, speedOfLight)
        val dotProduct: Double = initialVelocity.dotUnitVelocity(targetDirection)

        val speedOfLight2 = speedOfLight * speedOfLight

        val speedOfLight4 = speedOfLight2 * speedOfLight2

        val tmp1: Double = ((initialRestMass * initialRestMass + finalRestMass * finalRestMass) /
                2.0 / initialGamma / initialRestMass / finalRestMass)
        val tmp2: Double = tmp1 * tmp1

        val a: Double = dotProduct * dotProduct + tmp2 * speedOfLight2
        val b: Double = -2.0 * speedOfLight2 * dotProduct
        val c: Double = speedOfLight4 * (1 - tmp2)

        return discriminant(a, b, c) >= 0
    }

    fun changeVelocityByPhotonRocket(
        initialRestMass: Double,
        deltaRestMass: Double,
        initialVelocity: Velocity,
        targetDirection: Velocity,
        speedOfLight: Double,
    ) {
        val finalRestMass: Double = initialRestMass - deltaRestMass
        val initialGamma: Double = gamma(initialVelocity, speedOfLight)
        val dotProduct: Double = initialVelocity.dotUnitVelocity(targetDirection)
    }
}