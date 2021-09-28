package relativitization.universe.maths.algebra

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

object Quadratic {
    fun discriminant(a: Double, b: Double, c: Double): Double {
        return b * b - 4.0 * a * c
    }

    fun solveQuadratic(a: Double, b: Double, c: Double): QuadraticSolutions {
        val d: Double = discriminant(a, b, c)
        return if (d < 0) {
            QuadraticSolutions(false, 0, 0.0, 0.0)
        } else {
            val sol1: Double = (-b + sqrt(d)) / 2.0 / a
            val sol2: Double = (-b - sqrt(d)) / 2.0 / a

            val x1: Double = max(sol1, sol2)
            val x2: Double = min(sol1, sol2)

            when {
                x2 > 0 -> {
                    QuadraticSolutions(true, 2, x1, x2)
                }
                x1 > 0 -> {
                    QuadraticSolutions(true, 1, x1, x2)
                }
                else -> {
                    QuadraticSolutions(true, 0, x1, x2)
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
 * @property x1 solution 1, should be greater than solution 2
 * @property x2 solution 2
 */
data class QuadraticSolutions(
    val isRealSolutionExist: Boolean,
    val numPositiveSolution: Int,
    val x1: Double,
    val x2: Double
)