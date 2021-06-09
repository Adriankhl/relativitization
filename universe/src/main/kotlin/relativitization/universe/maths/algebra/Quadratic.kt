package relativitization.universe.maths.algebra

import kotlin.math.max
import kotlin.math.min

object Quadratic {
    fun discriminant(a: Double, b: Double, c: Double): Double {
        return b * b - 4.0 * a * c
    }

    fun solveQuadratic(a: Double, b: Double, c: Double): QuadraticSolutions {
        val d: Double = discriminant(a, b, c)
        return if (d < 0) {
            QuadraticSolutions(false, 0, 0.0, 0.0)
        } else {
            val sol1: Double = (-b + d) / 2.0 / a
            val sol2: Double = (-b - d) / 2.0 / a

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