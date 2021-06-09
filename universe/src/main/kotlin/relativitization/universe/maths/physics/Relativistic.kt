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

    /**
    * @param initialRestMass initial rest mass
    * @param deltaRestMass change of rest mass
    * @param initialVelocity initial velocity
    * @param targetDirection the target direction
    * @param speedOfLight speed of light
    */
    fun canTargetVelocityAtDirectionByPhotonRocket(
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

    /**
     * Compute the target velocity given a direction
     *
     * @param initialRestMass initial rest mass
     * @param deltaRestMass change of rest mass
     * @param initialVelocity initial velocity
     * @param targetDirection the target direction
     * @param accelerate should the final velocity be greater than the initial velocity
     * @param speedOfLight speed of light
     */
    fun targetVelocityAtDirectionPhotonRocket(
        initialRestMass: Double,
        deltaRestMass: Double,
        initialVelocity: Velocity,
        targetDirection: Velocity,
        accelerate: Boolean,
        speedOfLight: Double,
    ): VelocityChangeData {
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

        // Return this if anything is wrong
        val failVelocityChangeData: VelocityChangeData = VelocityChangeData(false, Velocity(0.0, 0.0, 0.0), 0.0)

        return if (solution.isRealSolutionExist) {
            when (solution.numPositiveSolution) {
                0 -> failVelocityChangeData
                1 -> if (accelerate) {
                    if (solution.x1 >= initialVelocity.mag()) {
                        VelocityChangeData(true, targetDirection.scaleVelocity(solution.x1), deltaRestMass)
                    } else {
                        failVelocityChangeData
                    }
                } else {
                    if (solution.x1 <= initialVelocity.mag()) {
                        VelocityChangeData(true, targetDirection.scaleVelocity(solution.x1), deltaRestMass)
                    } else {
                        failVelocityChangeData
                    }
                }
                2 -> if (accelerate) {
                    if (solution.x1 >= initialVelocity.mag()) {
                        VelocityChangeData(true, targetDirection.scaleVelocity(solution.x1), deltaRestMass)
                    } else {
                        failVelocityChangeData
                    }
                } else {
                    if (solution.x2 <= initialVelocity.mag()) {
                        VelocityChangeData(true, targetDirection.scaleVelocity(solution.x1), deltaRestMass)
                    } else {
                        failVelocityChangeData
                    }
                }
                // Shouldn't involve else
                else -> failVelocityChangeData
            }
        } else {
            failVelocityChangeData
        }
    }

    /**
     * Compute speed change by photon rocket when initial velocity is zero
     *
     * @param initialRestMass initial rest mass
     * @param deltaRestMass change of rest mass
     * @param speedOfLight speed of light
     */
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
            // Return initial rest mass in case if the returned value is used for further computation
            // The returned value should be checked to see if it is smaller than the initial rest mass or not
            initialRestMass
        }
    }


    /**
     * Given the parameters, decelerate the object
     *
     * @param initialRestMass initial reset mass of the object
     * @param initialVelocity initial velocity of the object
     * @param maxDeltaRestMass maximum possible change of rest mass
     * @param speedOfLight speed of light
     */
    fun decelerateByPhotonRocket(
        initialRestMass: Double,
        maxDeltaRestMass: Double,
        initialVelocity: Velocity,
        speedOfLight: Double,
    ): VelocityChangeData {
        val zeroVelocityDeltaRestMass: Double = deltaMassByPhotonRocket(
            initialRestMass = 0.0,
            initialVelocity = initialVelocity,
            targetVelocity = Velocity(0.0, 0.0, 0.0),
            speedOfLight = 0.0
        )

        return if (zeroVelocityDeltaRestMass <= maxDeltaRestMass) {
            VelocityChangeData(true, Velocity(0.0, 0.0, 0.0), zeroVelocityDeltaRestMass)
        } else {
            val targetData: VelocityChangeData = targetVelocityAtDirectionPhotonRocket(
                initialRestMass = initialRestMass,
                deltaRestMass = maxDeltaRestMass,
                initialVelocity = initialVelocity,
                targetDirection = initialVelocity.scaleVelocity(1.0),
                accelerate = false,
                speedOfLight = speedOfLight
            )

            if (!targetData.success) {
                logger.error("decelerateByPhotonRocket fail, something is wrong")
            }

            targetData
        }
    }


    /**
     * Try to change to the target velocity
     *
     * @param initialRestMass initial rest mass
     * @param maxDeltaRestMass the maximal possible change of rest mass
     * @param initialVelocity initial velocity
     * @param targetVelocity the target velocity
     * @param speedOfLight speed of light
     */
    fun targetVelocityByPhotonRocket(
        initialRestMass: Double,
        maxDeltaRestMass: Double,
        initialVelocity: Velocity,
        targetVelocity: Velocity,
        speedOfLight: Double,
    ): VelocityChangeData {
        val requiredDeltaMass: Double = deltaMassByPhotonRocket(
            initialRestMass = 0.0,
            initialVelocity = initialVelocity,
            targetVelocity = targetVelocity,
            speedOfLight = speedOfLight,
        )

        return if (requiredDeltaMass <= maxDeltaRestMass) {
            VelocityChangeData(true, targetVelocity, requiredDeltaMass)
        } else {
            // Try to change to the correct direction and accelerate / decelerate
            val changeToDirectionData: VelocityChangeData = targetVelocityAtDirectionPhotonRocket(
                initialRestMass = initialRestMass,
                deltaRestMass = maxDeltaRestMass,
                initialVelocity = initialVelocity,
                targetDirection = targetVelocity,
                accelerate = initialVelocity.squareMag() < targetVelocity.squareMag(),
                speedOfLight = speedOfLight
            )
            if (changeToDirectionData.success) {
                changeToDirectionData
            } else {
                decelerateByPhotonRocket(
                    initialRestMass = initialRestMass,
                    maxDeltaRestMass = maxDeltaRestMass,
                    initialVelocity = initialVelocity,
                    speedOfLight = speedOfLight,
                )
            }
        }
    }
}

data class VelocityChangeData(
    val success: Boolean,
    val newVelocity: Velocity,
    val deltaRestMass: Double,
)