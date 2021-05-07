package relativitization.universe.maths.grid

import org.apache.logging.log4j.LogManager
import relativitization.universe.data.physics.Int2D
import relativitization.universe.data.physics.Int3D
import relativitization.universe.maths.grid.Grids.isInt3DValid
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
     * @return function converting key of the gridMap and id in the gridMap value to Rectangle, separated by space
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
        // Multiply by two to leave space between grids
        val numGridDivision: Int = numDivisionInGroup(gridMap.size * 2)

        val gridRectangleFunction: (Int) -> IntRectangle = indexToRectangleFunction(
            numRequiredRectangle = numGridDivision * numGridDivision,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            groupWidth = gridWidth,
            groupHeight = gridHeight,
            xOffSet = xOffSet,
            yOffSet = yOffSet
        )

        val mapIdToIndex: Map<Int, Int> = gridMap.keys.toList().sorted().mapIndexed { idx, value ->
            // convert index to realIndex which is separated by empty grid
            val realIdx: Int = if ((idx * 2 / numGridDivision) % 2 == 0) {
                idx * 2
            } else {
                idx * 2 + 1
            }
            value to realIdx
        }.toMap()

        val rectangleFunctionMap: Map<Int, (Int) -> IntRectangle> = gridMap.mapValues {
            val innerRectangle: IntRectangle = gridRectangleFunction(mapIdToIndex.getValue(it.key))
            indexToRectangleFunction(
                numRequiredRectangle = numGridDivision * numGridDivision,
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
            if (listIndex == -1) {
                logger.error("idAtGridToRectangleFunction: incorrect index")
            }
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
     * @return function mapping position to id, the function return -1 if the id does not exist
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
        // Multiply by two to leave space between grids
        val numGridDivision: Int = numDivisionInGroup(gridMap.size * 2)

        val gridRectangleFunction: (Int) -> IntRectangle = indexToRectangleFunction(
            numRequiredRectangle = numGridDivision * numGridDivision,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            groupWidth = gridWidth,
            groupHeight = gridHeight,
            xOffSet = xOffSet,
            yOffSet = yOffSet
        )

        val positionToGridIndexFunction: (Int, Int) -> Int = positionToIndexFunction(
            numRequiredRectangle = numGridDivision * numGridDivision,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            groupWidth = gridWidth,
            groupHeight = gridHeight,
            xOffSet = xOffSet,
            yOffSet = yOffSet
        )

        val mapIdToIndex: Map<Int, Int> = gridMap.keys.toList().sorted().mapIndexed { idx, value ->
            // convert index to realIndex which is separated by empty grid
            val realIdx: Int = if ((idx * 2 / numGridDivision) % 2 == 0) {
                idx * 2
            } else {
                idx * 2 + 1
            }
            value to realIdx
        }.toMap()

        val mapIndexToId: Map<Int, Int> = mapIdToIndex.entries.associate { it.value to it.key }

        val positionFunctionMap = gridMap.mapValues {
            val innerRectangle: IntRectangle = gridRectangleFunction(mapIdToIndex.getValue(it.key))
            positionToIndexFunction(
                numRequiredRectangle = numGridDivision * numGridDivision,
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                groupWidth = innerRectangle.width,
                groupHeight = innerRectangle.height,
                xOffSet = innerRectangle.xPos,
                yOffSet = innerRectangle.yPos,
            )
        }

        return { xPos, yPos ->
            // check if this is within range
            if ((xPos >= xOffSet) && (xPos < xOffSet + gridWidth) &&
                (yPos >= yOffSet) && (yPos < yOffSet + gridHeight)
            ) {
                val gridIndex: Int = positionToGridIndexFunction(xPos, yPos)
                if (mapIndexToId.containsKey(gridIndex)) {
                    val mapId: Int = mapIndexToId.getValue(gridIndex)
                    val listIndex: Int = positionFunctionMap.getValue(mapId)(xPos, yPos)
                    // return -1 if out of bound
                    gridMap.getValue(mapId).getOrElse(listIndex) { -1 }
                } else {
                    -1
                }
            } else {
                -1
            }
        }
    }

    /**
     * Compute grid scaling factor by the cell with maximum required rectangles
     */
    fun gridScaleFactor(
        data3D: List<List<List<Map<Int, List<Int>>>>>,
    ): Int {
        return data3D.flatten().flatten().map { gridMap ->
            val gridDivision: Int = numDivisionInGroup(gridMap.size)
            val maxInnerDivision: Int = gridMap.values.map { idList ->
                numDivisionInGroup(idList.size)
            }.maxOrNull() ?: 1
            maxInnerDivision * gridDivision
        }.maxOrNull() ?: 1
    }

    /**
     * Compute rectangle for grid inside grid
     *
     * @param data3D cropped universe 3d view data
     * @param imageWidth height of the texture image
     * @param imageHeight width of the texture image
     * @param gridXSeparation the unscaled spacing in x axis between grid of different z coordinate
     * @param gridYSeparation the unscaled spacing in Y axis between grid of different z coordinate
     * @param xOffSet offset in x coordinate
     * @param yOffSet offset in y coordinate
     *
     * @return function converting key of the gridMap and id in the gridMap value to Rectangle
     */
    fun data3DToRectangleFunction(
        data3D: List<List<List<Map<Int, List<Int>>>>>,
        imageWidth: Int,
        imageHeight: Int,
        gridXSeparation: Int,
        gridYSeparation: Int,
        xOffSet: Int,
        yOffSet: Int,
    ): (Int3D, Int, Int) -> IntRectangle {
        val scale = gridScaleFactor(data3D)
        val gridWidth = imageWidth * scale
        val gridHeight = imageHeight * scale
        val zDim: Int = data3D.flatten().maxOfOrNull { zList -> zList.size } ?: 1

        // Compute the separation of grid with different z separated + a grid dimension
        val xSingleSpace: Int = gridXSeparation * scale + gridWidth
        val ySingleSpace: Int = gridYSeparation * scale + gridHeight

        // Space between grid with different x and y coordinate
        val xyFullSpace = max(xSingleSpace * (zDim + 1), ySingleSpace * (zDim + 1))

        val data3DToRectangleAtGridFunction: List<List<List<(Int, Int) -> IntRectangle>>> = data3D.mapIndexed { x, yList ->
            yList.mapIndexed { y, zList ->
                zList.mapIndexed { z, gridMap ->
                    val gridXOffSet: Int = xOffSet + x * xyFullSpace + z * xSingleSpace
                    val gridYOffSet: Int = yOffSet + y * xyFullSpace + z * ySingleSpace
                    idAtGridToRectangleFunction(
                        gridMap = gridMap,
                        imageWidth = imageWidth,
                        imageHeight = imageHeight,
                        gridWidth = gridWidth,
                        gridHeight = gridHeight,
                        xOffSet = gridXOffSet,
                        yOffSet = gridYOffSet,
                    )
                }
            }
        }

        return { int3D: Int3D, mapId: Int, id:Int ->
            if (isInt3DValid(int3D, data3D)) {
                logger.error("data3DToRectangleFunction: Invalid int3D")
            }
            data3DToRectangleAtGridFunction[int3D.x][int3D.y][int3D.z](mapId, id)
        }
    }


    /**
     * Compute rectangle for grid inside grid
     *
     * @param data3D cropped universe 3d view data
     * @param imageWidth height of the texture image
     * @param imageHeight width of the texture image
     * @param gridXSeparation the unscaled spacing in x axis between grid of different z coordinate
     * @param gridYSeparation the unscaled spacing in Y axis between grid of different z coordinate
     * @param xOffSet offset in x coordinate
     * @param yOffSet offset in y coordinate
     *
     * @return function converting position to id
     */
    fun positionToIdAtData3DFunction(
        data3D: List<List<List<Map<Int, List<Int>>>>>,
        imageWidth: Int,
        imageHeight: Int,
        gridXSeparation: Int,
        gridYSeparation: Int,
        xOffSet: Int,
        yOffSet: Int,
    ): (Int, Int) -> Int {
        val scale = gridScaleFactor(data3D)
        val gridWidth = imageWidth * scale
        val gridHeight = imageHeight * scale

        val xDim = data3D.size
        val yDim = data3D.maxOfOrNull { yList -> yList.size } ?: 1
        val zDim: Int = data3D.flatten().maxOfOrNull { zList -> zList.size } ?: 1

        // Compute the separation of grid with different z separated + a grid dimension
        val xSingleSpace: Int = gridXSeparation * scale + gridWidth
        val ySingleSpace: Int = gridYSeparation * scale + gridHeight

        // Space between grid with different x and y coordinate
        val xyFullSpace = max(xSingleSpace * (zDim + 1), ySingleSpace * (zDim + 1))

        val data3DToRectangleAtGridFunction: List<List<List<(Int, Int) -> IntRectangle>>> = data3D.mapIndexed { x, yList ->
            yList.mapIndexed { y, zList ->
                zList.mapIndexed { z, gridMap ->
                    val gridXOffSet: Int = xOffSet + x * xyFullSpace + z * xSingleSpace
                    val gridYOffSet: Int = yOffSet + y * xyFullSpace + z * ySingleSpace
                    idAtGridToRectangleFunction(
                        gridMap = gridMap,
                        imageWidth = imageWidth,
                        imageHeight = imageHeight,
                        gridWidth = gridWidth,
                        gridHeight = gridHeight,
                        xOffSet = gridXOffSet,
                        yOffSet = gridYOffSet,
                    )
                }
            }
        }

        return { xPos, yPos ->
            // Compute x, y ,z index, this may not be the true index if the position is out of all the rectangles
            val x: Int = (xPos - xOffSet) / xyFullSpace
            val y: Int = (yPos - yOffSet) / xyFullSpace
            val z: Int = ((xPos - xOffSet) % xyFullSpace) / xSingleSpace
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