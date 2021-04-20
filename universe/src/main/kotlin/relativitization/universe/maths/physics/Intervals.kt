package relativitization.universe.maths.physics

import relativitization.universe.data.physics.Int3D
import relativitization.universe.data.physics.Int4D
import relativitization.universe.data.physics.Velocity
import kotlin.math.abs
import kotlin.math.sqrt

object Intervals {
    fun distance(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double =
            sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) + (z2 - z1) * (z2 - z1))

    /**
     * Compute the maximum distance between the farthest vertices of 2 cubes
     * then round up to Int and return the maximum Int distance
     */
    fun intDistanceFromOrigin(x: Int, y: Int, z: Int): Int {
        val doubleDistance = distance(
                0.0, 0.0, 0.0,
                x.toDouble() + 1.0, y.toDouble() + 1.0, z.toDouble() + 1.0
        )

        return (doubleDistance - 0.000001).toInt() + 1 // Round up
    }

    /**
     * Compute distance between two cubes
     * round up the distance to prevent faster than light
     */
    fun intDistance(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int): Int {
        val dx = abs(x2 - x1)
        val dy = abs(y2 - y1)
        val dz = abs(z2 - z1)

        return intDistanceFromOrigin(dx, dy, dz)
    }

    /**
     * Compute distance by IntCoordinates
     */
    fun intDistance(c1: Int4D, c2: Int4D): Int {
        return intDistance(c1.x, c1.y, c1.z, c2.x, c2.y, c2.z)
    }

    /**
     * Compute distance by IntCoordinates
     */
    fun intDistance(c1: Int3D, c2: Int3D): Int {
        return intDistance(c1.x, c1.y, c1.z, c2.x, c2.y, c2.z)
    }

    /**
     * Compute light travel time by turn, round up
     */
    fun intDelay(c1: Int3D, c2: Int3D, speedOfLight: Int): Int {
        return (intDistance(c1, c2) - 1) / speedOfLight + 1
    }

    /**
     * Time after dilation
     */
    fun dilatedTime(dt: Double, velocity: Velocity, speedOfLight: Int): Double {
        return dt / gamma(velocity, speedOfLight)
    }

}
