package relativitization.universe.maths.grid

import org.apache.logging.log4j.LogManager
import relativitization.universe.data.physics.Int2D
import kotlin.math.max
import kotlin.math.min

object Projections {

    /**
     * Compute the required number of division to store rectangles in a group given the number of required rectangle
     *
     * @param numRequiredRectangle the number of required rectangle in this group
     * @param divAcc for recursive computation of the number of division in an axis of the group
     */
    private fun numDivisionInGroup(numRequiredRectangle: Int, divAcc: Int = 1): Int {
        return if (numRequiredRectangle < divAcc * divAcc) {
            divAcc
        } else {
            numDivisionInGroup(numRequiredRectangle, 2 * divAcc)
        }
    }

    /**
     * Coordinates of rectangles in a single group
     *
     * @return function to convert index to rectangle
     */
    fun indexToRectangleFunction(
        numRequiredRectangle: Int,
        imageWidth: Int,
        imageHeight: Int,
        groupWidth: Int,
        groupHeight: Int,
        xOffSet: Int,
        yOffSet: Int,
    ): (Int) -> IntRectangle {
        val numDivision: Int = numDivisionInGroup(numRequiredRectangle)
        val totalImageWidth: Int = imageWidth * numDivision
        val totalImageHeight: Int = imageHeight * numDivision
        val scale: Int = min(groupWidth / totalImageWidth, groupHeight / totalImageHeight)

        // If the grid size is smaller than the total required image size, something is wrong
        if (scale == 0) {
            logger.error("rectangleListInGroup error: scale = 0")
        }

        val scaledWidth: Int = imageWidth * scale
        val scaledHeight: Int = imageHeight * scale

        return { rectangleIndex ->
            val x = rectangleIndex % numDivision
            val y = rectangleIndex / numDivision
            IntRectangle(xOffSet + scaledWidth * x, yOffSet + scaledHeight * y, scaledWidth, scaledHeight)
        }
    }

    /**
     * From position to index in a group
     *
     * @return a function from x and y position to index
     */
    fun positionToIndexFunction(
        numRequiredRectangle: Int,
        imageWidth: Int,
        imageHeight: Int,
        groupWidth: Int,
        groupHeight: Int,
        xOffSet: Int,
        yOffSet: Int,
    ): (Int, Int) -> Int {
        val numDivision: Int = numDivisionInGroup(numRequiredRectangle)
        val totalImageWidth: Int = imageWidth * numDivision
        val totalImageHeight: Int = imageHeight * numDivision
        val scale: Int = min(groupWidth / totalImageWidth, groupHeight / totalImageHeight)

        // If the grid size is smaller than the total required image size, something is wrong
        if (scale == 0) {
            logger.error("positionToIndexInGroup error: scale = 0")
        }

        val scaledWidth: Int = imageWidth * scale
        val scaledHeight: Int = imageHeight * scale

        return { xPos: Int, yPos: Int ->
            val xIndex: Int = (xPos - xOffSet) / scaledWidth
            val yIndex: Int = (yPos - yOffSet) / scaledHeight
            yIndex * numDivision + xIndex
        }
    }

    /**
     * Compute rectangle for grid inside grid
     *
     * @param gridMap a map of grid indexes to lists of image id
     * @param imageWidth height of the texture image
     * @param imageHeight width of the texture image
     * @param gridWidth width of this grid
     * @param gridHeight height of this grid
     * @param xOffSet offset in x coordinate
     * @param yOffSet offset in y coordinate
     *
     * @return function converting key of the gridMap and id in the gridMap value to Rectangle
     */
    fun idAtGridToRectangleFunction(
        gridMap: Map<Int, List<Int>>,
        imageWidth: Int,
        imageHeight: Int,
        gridWidth: Int,
        gridHeight: Int,
        xOffSet: Int,
        yOffSet: Int,
    ): (Int, Int) -> IntRectangle {
        val gridRectangleFunction: (Int) -> IntRectangle = indexToRectangleFunction(
            numRequiredRectangle = gridMap.size,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            groupWidth = gridWidth,
            groupHeight = gridHeight,
            xOffSet = xOffSet,
            yOffSet = yOffSet
        )

        val mapIdToIndex: Map<Int, Int> = gridMap.keys.toList().sorted().mapIndexed { idx, value -> value to idx }.toMap()

        val rectangleFunctionMap: Map<Int, (Int) -> IntRectangle> = gridMap.mapValues {
            val innerRectangle: IntRectangle = gridRectangleFunction(mapIdToIndex.getValue(it.key))
            indexToRectangleFunction(
                numRequiredRectangle = it.value.size,
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                groupWidth = innerRectangle.width,
                groupHeight = innerRectangle.height,
                xOffSet = innerRectangle.xPos,
                yOffSet = innerRectangle.yPos,
            )
        }

        return { mapId, id ->
            val listIndex = gridMap.getValue(mapId).indexOf(id)
            rectangleFunctionMap.getValue(mapId)(listIndex)
        }
    }

    /**
     * Compute rectangle for grid inside grid
     *
     * @param gridMap a map of grid indexes to lists of image id
     * @param imageWidth height of the texture image
     * @param imageHeight width of the texture image
     * @param gridWidth width of this grid
     * @param gridHeight height of this grid
     * @param xOffSet offset in x coordinate
     * @param yOffSet offset in y coordinate
     *
     * @return function of map id and list value to rectangle, the function return -1 if the id does not exist
     */
    fun positionToIdAtGridFunction(
        gridMap: Map<Int, List<Int>>,
        imageWidth: Int,
        imageHeight: Int,
        gridWidth: Int,
        gridHeight: Int,
        xOffSet: Int,
        yOffSet: Int,
    ): (Int, Int) -> Int {
        val gridRectangleFunction: (Int) -> IntRectangle = indexToRectangleFunction(
            numRequiredRectangle = gridMap.size,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            groupWidth = gridWidth,
            groupHeight = gridHeight,
            xOffSet = xOffSet,
            yOffSet = yOffSet
        )

        val positionToGridIndexFunction: (Int, Int) -> Int = positionToIndexFunction(
            numRequiredRectangle = gridMap.size,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            groupWidth = gridWidth,
            groupHeight = gridHeight,
            xOffSet = xOffSet,
            yOffSet = yOffSet
        )

        val mapIndexToId: Map<Int, Int> = gridMap.keys.toList().sorted().mapIndexed { idx, value -> idx to value }.toMap()

        val mapIdToIndex: Map<Int, Int> = gridMap.keys.toList().sorted().mapIndexed { idx, value -> value to idx }.toMap()

        val positionFunctionMap = gridMap.mapValues {
            val innerRectangle: IntRectangle = gridRectangleFunction(mapIdToIndex.getValue(it.key))
            positionToIndexFunction(
                numRequiredRectangle = it.value.size,
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                groupWidth = innerRectangle.width,
                groupHeight = innerRectangle.height,
                xOffSet = innerRectangle.xPos,
                yOffSet = innerRectangle.yPos,
            )
        }

        return { xPos, yPos ->
            val gridIndex: Int = positionToGridIndexFunction(xPos - xOffSet, yPos - yOffSet)
            if (mapIndexToId.containsKey(gridIndex)) {
                val mapId: Int = mapIndexToId.getValue(gridIndex)
                val listIndex: Int = positionFunctionMap.getValue(mapId)(xPos, yPos)
                gridMap.getValue(mapId).get(listIndex)
            } else {
                -1
            }
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

