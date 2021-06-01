package relativitization.universe.maths.physics

import relativitization.universe.data.physics.Double3D
import relativitization.universe.data.physics.Int3D
import relativitization.universe.data.physics.Int4D
import relativitization.universe.data.physics.MutableDouble4D
import relativitization.universe.data.physics.MutableInt4D
import relativitization.universe.data.physics.Velocity
import relativitization.universe.maths.physics.Relativistic.gamma
import kotlin.math.abs
import kotlin.math.sqrt

object Intervals {
    fun distance(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double =
        sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) + (z2 - z1) * (z2 - z1))

    fun distance(d1: MutableDouble4D, d2: MutableDouble4D): Double =
        distance(d1.x, d1.y, d1.z, d2.z, d2.y, d2.z)

    /**
     * Compute the maximum distance between the farthest vertices of 2 cubes
     */
    fun doubleDistanceFromOrigin(x: Int, y: Int, z: Int): Double {
        return distance(
            0.0, 0.0, 0.0,
            x.toDouble() + 1.0, y.toDouble() + 1.0, z.toDouble() + 1.0
        )
    }

    /**
     * Compute the maximum distance between the farthest vertices of 2 cubes
     * then round up to Int and return the maximum Int distance
     */
    fun intDistanceFromOrigin(x: Int, y: Int, z: Int): Int {
        return (doubleDistanceFromOrigin(x, y, z) - 0.000001).toInt() + 1 // Round up
    }

    /**
     * Compute distance between two cubes
     */
    fun doubleDistance(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int): Double {
        val dx = abs(x2 - x1)
        val dy = abs(y2 - y1)
        val dz = abs(z2 - z1)

        return doubleDistanceFromOrigin(dx, dy, dz)
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

    fun intDistance(c1: MutableInt4D, c2: Int4D): Int {
        return intDistance(c1.x, c1.y, c1.z, c2.x, c2.y, c2.z)
    }

    fun intDistance(c1: Int4D, c2: MutableInt4D): Int {
        return intDistance(c1.x, c1.y, c1.z, c2.x, c2.y, c2.z)
    }

    /**
     * Compute distance by IntCoordinates
     */
    fun doubleDistance(c1: Int3D, c2: Int3D): Double {
        return doubleDistance(c1.x, c1.y, c1.z, c2.x, c2.y, c2.z)
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
    fun intDelay(c1: Int3D, c2: Int3D, speedOfLight: Double): Int {
        return (doubleDistance(c1, c2) / speedOfLight - 0.000001).toInt() + 1
    }

    /**
     * Maximum time delay change when a player move to an adjacent grid
     */
    fun maxDelayAfterMove(speedOfLight: Double): Int {
        return intDelay(Int3D(0, 0, 0), Int3D(1, 1, 1), speedOfLight)
    }

    /**
     * Time after dilation
     */
    fun dilatedTime(dt: Double, velocity: Velocity, speedOfLight: Double): Double {
        return dt / gamma(velocity, speedOfLight)
    }

    /**
     * Calculate target velocity by position
     *
     * @return target velocity at speed of light
     */
    fun targetVelocity(from: Double3D, to: Double3D): Velocity {
        val vx = to.x - from.x
        val vy = to.y - from.y
        val vz = to.z - from.z
        return Velocity(vx, vy, vz).scaleVelocity(1.0)
    }
}
