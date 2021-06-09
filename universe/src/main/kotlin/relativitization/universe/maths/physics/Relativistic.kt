package relativitization.universe.maths.physics

import org.apache.logging.log4j.LogManager
import relativitization.universe.data.physics.Velocity
import relativitization.universe.maths.algebra.Quadratic.discriminant
import relativitization.universe.maths.algebra.Quadratic.solveQuadratic
import relativitization.universe.maths.algebra.QuadraticSolutions
import kotlin.math.sqrt

object Relativistic {
    private val logger = LogManager.getLogger()

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

    fun canTargetDirectionByPhotonRocket(
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

    fun targetDirectionByPhotonRocket(
        initialRestMass: Double,
        deltaRestMass: Double,
        initialVelocity: Velocity,
        targetDirection: Velocity,
        accelerate: Boolean,
        speedOfLight: Double,
    ): Velocity {
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

        val solution: QuadraticSolutions = solveQuadratic(a, b, c)

        return if (solution.isRealSolutionExist) {
            when (solution.numPositiveSolution) {
                0 -> Velocity(0.0, 0.0, 0.0)
                1 -> targetDirection.scaleVelocity(solution.x1)
                2 -> if (accelerate) {
                    targetDirection.scaleVelocity(solution.x1)
                } else {
                    targetDirection.scaleVelocity(solution.x2)
                }
                // Shouldn't involve else
                else -> Velocity(0.0, 0.0, 0.0)
            }
        } else {
            Velocity(0.0, 0.0, 0.0)
        }
    }

    fun speedByPhotonRocket(
        initialRestMass: Double,
        deltaRestMass: Double,
        speedOfLight: Double,
    ): Double {
        val finalMass: Double = initialRestMass - deltaRestMass
        val ratio: Double = initialRestMass / finalMass
        return speedOfLight * (ratio * ratio - 1) / (ratio * ratio + 1)
    }

    /**
     * Given the parameters, compute the change of mass needed to change to target velocity
     *
     * @param initialRestMass initial reset mass of the object
     * @param initialVelocity initial velocity of the object
     * @param targetVelocity target velocity
     * @param speedOfLight speed of light
     */
    fun deltaMassByPhotonRocket(
        initialRestMass: Double,
        initialVelocity: Velocity,
        targetVelocity: Velocity,
        speedOfLight: Double,
    ): Double {
        val speedOfLight2 = speedOfLight * speedOfLight
        val initialGamma: Double = gamma(initialVelocity, speedOfLight)
        val finalGamma: Double = gamma(targetVelocity, speedOfLight)
        val a: Double = speedOfLight2
        val b: Double = -2.0 * initialGamma * finalGamma * initialRestMass * (speedOfLight2 - initialVelocity.dot(targetVelocity))
        val c: Double = initialRestMass * initialRestMass * speedOfLight2

        // Solution of final rest mass
        val solution: QuadraticSolutions = solveQuadratic(a, b, c)

        return if ((solution.x2 <= initialRestMass) && (solution.x2 >= 0)) {
            initialRestMass - solution.x2
        } else if ((solution.x1 <= initialRestMass) && (solution.x1 >= 0)) {
            initialRestMass - solution.x1
        } else {
            logger.error("Wrong delta mass computed")
            -1.0
        }
    }
}