package relativitization.universe.maths.grid

import org.apache.logging.log4j.LogManager
import relativitization.universe.data.physics.Int2D
import kotlin.math.max
import kotlin.math.min

object Projections {

    /**
     * Compute the required number of division to store rectangles in a group given the maximum index
     *
     * @param maxRectIndex the number of required rectangle in this group
     * @param divAcc for recursive computation of the number of division in an axis of the group
     */
    private fun numDivisionInGroup(maxRectIndex: Int, divAcc: Int = 1): Int {
        return if (maxRectIndex < divAcc * divAcc) {
            divAcc
        } else {
            numDivisionInGroup(maxRectIndex, 2 * divAcc)
        }
    }

    /**
     * Coordinate in a single group
     */
    fun rectangleListInGroup(
        maxRectangleIndex: Int,
        imageWidth: Int,
        imageHeight: Int,
        groupWidth: Int,
        groupHeight: Int,
        xOffSet: Int,
        yOffSet: Int,
    ): List<IntRectangle> {
        val numDivision: Int = numDivisionInGroup(maxRectangleIndex)
        val totalImageWidth: Int = imageWidth * numDivision
        val totalImageHeight: Int = imageHeight * numDivision
        val scale: Int = min(groupWidth / totalImageWidth, groupHeight / totalImageHeight)

        // If the grid size is smaller than the total required image size, something is wrong
        if (scale == 0) {
            logger.error("rectangleListInGroup error: scale = 0")
        }

        val scaledWidth: Int = imageWidth * scale
        val scaledHeight: Int = imageHeight * scale

        return (0..maxRectangleIndex).map {
            val x = it % numDivision
            val y = it / numDivision
            IntRectangle(xOffSet + scaledWidth * x, yOffSet + scaledHeight * y, scaledWidth, scaledHeight)
        }
    }

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

    private val logger = LogManager.getLogger()
}

