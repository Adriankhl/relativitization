package relativitization.universe.maths.grid

import relativitization.universe.data.physics.Int2D
import relativitization.universe.data.physics.Int3D
import kotlin.math.max

object Projections {

    /**
     * Project a 3D coordinate to 2D pixel plane
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param zDim the dimension of the z coordinate
     * @param gridWidth width of a grid in pixel
     * @param gridHeight height of a grid in pixel
     * @param xSpace x separation (in number of grid) between projected grid in z dimension
     * @param ySpace y separation (in number of grid) between projected grid in z dimension
     * @param xExtra extra spaces in the x-axis after all projected grid, in number of grid
     * @param yExtra extra spaces in the y-axis after all projected grid, in number of grid
     */
    fun coordinate2DFrom3D(
        x: Int,
        y: Int,
        z: Int,
        zDim: Int,
        gridWidth: Int,
        gridHeight: Int,
        xSpace: Int,
        ySpace: Int,
        xExtra: Int,
        yExtra: Int
    ): Int2D {

        val xSeparation = if (xSpace != ySpace) {
            (zDim + xExtra) * (xSpace + 1) * gridWidth
        } else {
            (zDim + xExtra) * 2 * (xSpace + 1) * gridWidth
        }

        val ySeparation = if (xSpace != ySpace) {
            (zDim + yExtra) * (ySpace + 1) * gridHeight
        } else {
            (zDim + yExtra) * 2 * (ySpace + 1) * gridHeight
        }

        val separation = max(xSeparation, ySeparation)

        val xPos = x * separation + z * (xSpace + 1) * gridWidth

        val yPos = y * separation + z * (ySpace + 1) * gridHeight

        return Int2D(xPos, yPos)
    }
}

