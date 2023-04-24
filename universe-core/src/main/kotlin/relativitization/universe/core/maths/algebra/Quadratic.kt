package relativitization.universe.core.maths.algebra

import relativitization.universe.core.utils.RelativitizationLogManager
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

object Quadratic {
    private val logger = RelativitizationLogManager.getLogger()

    fun discriminant(a: Double, b: Double, c: Double): Double {
        return b * b - 4.0 * a * c
    }

    fun solveQuadratic(a: Double, b: Double, c: Double): QuadraticSolutions {
        val originalDiscriminant: Double = discriminant(a, b, c)
        val d: Double = if ((originalDiscriminant < 0.0) && (originalDiscriminant > -0.001)) {
            logger.debug("Round negative discriminant to 0.0. ")
            0.0
        } else {
            originalDiscriminant
        }
        return when {
            d < 0.0 -> {
                QuadraticSolutions(false, 0, 0.0, 0.0)
            }
            d == 0.0 -> {
                val sol: Double = -b / 2.0 / a
                QuadraticSolutions(true, 2, sol, sol)
            }
            else -> {
                val sol1: Double = (-b + sqrt(d)) / 2.0 / a
                val sol2: Double = (-b - sqrt(d)) / 2.0 / a

                val maxSol: Double = max(sol1, sol2)
                val minSol: Double = min(sol1, sol2)
                when {
                    minSol > 0 -> {
                        QuadraticSolutions(true, 2, maxSol, minSol)
                    }
                    maxSol > 0 -> {
                        QuadraticSolutions(true, 1, maxSol, minSol)
                    }
                    else -> {
                        QuadraticSolutions(true, 0, maxSol, minSol)
                    }
                }
            }
        }
    }

    /**
     * Compute a standard quadratic function of x by enforcing specific criteria
     *
     * @param x the x coordinate of the quadratic curve
     * @param xMin the min value of x in the specific context
     * @param xMax the max value of x in the specific context
     * @param yMin the min value of y in the specific context
     * @param yMax the max value of y in the specific context
     * @param increasing whether y increases as a function of x
     * @param accelerate whether the change of y (increases or decreases) accelerates
     */
    fun standard(
        x: Double,
        xMin: Double,
        xMax: Double,
        yMin: Double,
        yMax: Double,
        increasing: Boolean = true,
        accelerate: Boolean = true,
    ): Double {
        val pX: Double = (x - xMin) / (xMax - xMin)
        val pY: Double = when {
            increasing && accelerate -> {
                pX * pX
            }
            increasing && !accelerate -> {
                1.0 - ((pX - 1.0) * (pX - 1.0))
            }
            !increasing && accelerate -> {
                1.0 - (pX * pX)
            }
            else -> {
                (pX - 1.0) * (pX - 1.0)
            }
        }

        return pY * (yMax - yMin) + yMin
    }
}

/**
 * To store solution of quadratic equation
 *
 * @property isRealSolutionExist is real solution exists
 * @property numPositiveSolution number of positive solution
 * @property maxSol solution 1, should be greater than or equal to solution 2
 * @property minSol solution 2
 */
data class QuadraticSolutions(
    val isRealSolutionExist: Boolean,
    val numPositiveSolution: Int,
    val maxSol: Double,
    val minSol: Double
)