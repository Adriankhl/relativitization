package relativitization.universe.maths.grid

import relativitization.universe.data.components.default.physics.Int3D
import relativitization.universe.maths.grid.Grids.isInt3DValid
import relativitization.universe.utils.RelativitizationLogManager
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
        return if (numRequiredRectangle <= divAcc * divAcc) {
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
            IntRectangle(
                xOffSet + scaledWidth * x,
                yOffSet + scaledHeight * y,
                scaledWidth,
                scaledHeight
            )
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
    private fun gridScaleFactor(
        data3D: List<List<List<Map<Int, List<Int>>>>>,
    ): Int {
        return data3D.flatten().flatten().map { gridMap ->
            // multiply by two to insert spacing
            val gridDivision: Int = numDivisionInGroup(gridMap.size * 2)
            val maxInnerDivision: Int = gridMap.values.map { idList ->
                numDivisionInGroup(idList.size)
            }.maxOrNull() ?: 1
            maxInnerDivision * gridDivision
        }.maxOrNull() ?: 1
    }

    /**
     * Function to for projecting data3D
     *
     * @param data3D cropped universe 3d view data
     * @param imageWidth height of the texture image
     * @param imageHeight width of the texture image
     * @param gridXSeparation the unscaled spacing in x axis between grid of different z coordinate
     * @param gridYSeparation the unscaled spacing in Y axis between grid of different z coordinate
     * @param xOffSet offset in x coordinate
     * @param yOffSet offset in y coordinate
     *
     * @return functions for calculating the projection
     */
    private fun createData3DProjectionFunction(
        data3D: List<List<List<Map<Int, List<Int>>>>>,
        imageWidth: Int,
        imageHeight: Int,
        gridXSeparation: Int,
        gridYSeparation: Int,
        xOffSet: Int,
        yOffSet: Int,
    ): Data3DProjectionFunction {
        val scale = gridScaleFactor(data3D)
        val gridWidth = imageWidth * scale
        val gridHeight = imageHeight * scale
        val zDim: Int = data3D.flatten().maxOfOrNull { zList -> zList.size } ?: 1

        // Compute the separation of grid with different z separated + a grid dimension
        val xSingleSpace: Int = gridXSeparation * scale + gridWidth
        val ySingleSpace: Int = gridYSeparation * scale + gridHeight

        // Space between grid with different x and y coordinate
        val xyFullSpace = max(xSingleSpace * (zDim + 1), ySingleSpace * (zDim + 1))

        val int3DRectangleData: List<List<List<IntRectangle>>> = data3D.mapIndexed { x, yList ->
            yList.mapIndexed { y, zList ->
                zList.mapIndexed { z, _ ->
                    val gridXOffSet: Int = xOffSet + x * xyFullSpace + z * xSingleSpace
                    val gridYOffSet: Int = yOffSet + y * xyFullSpace + z * ySingleSpace
                    IntRectangle(gridXOffSet, gridYOffSet, gridWidth, gridHeight)
                }
            }
        }

        val data3DToRectangleFunction: List<List<List<(Int, Int) -> IntRectangle>>> =
            int3DRectangleData.mapIndexed { x, yList ->
                yList.mapIndexed { y, zList ->
                    zList.mapIndexed { z, rectangle ->
                        idAtGridToRectangleFunction(
                            gridMap = data3D[x][y][z],
                            imageWidth = imageWidth,
                            imageHeight = imageHeight,
                            gridWidth = rectangle.width,
                            gridHeight = rectangle.height,
                            xOffSet = rectangle.xPos,
                            yOffSet = rectangle.yPos,
                        )
                    }
                }
            }

        val positionToIdAtData3DFunction: List<List<List<(Int, Int) -> Int>>> =
            int3DRectangleData.mapIndexed { x, yList ->
                yList.mapIndexed { y, zList ->
                    zList.mapIndexed { z, rectangle ->
                        positionToIdAtGridFunction(
                            gridMap = data3D[x][y][z],
                            imageWidth = imageWidth,
                            imageHeight = imageHeight,
                            gridWidth = rectangle.width,
                            gridHeight = rectangle.height,
                            xOffSet = rectangle.xPos,
                            yOffSet = rectangle.yPos,
                        )
                    }
                }
            }


        val int3DToRectangle: (Int3D) -> IntRectangle = { int3D ->
            if (!isInt3DValid(int3D, data3D)) {
                logger.error("int3DToRectangle: Invalid int3D")
            }
            int3DRectangleData[int3D.x][int3D.y][int3D.z]
        }

        val data3DToRectangle: (Int3D, Int, Int) -> IntRectangle =
            { int3D: Int3D, mapId: Int, id: Int ->
                if (!isInt3DValid(int3D, data3D)) {
                    logger.error("data3DToRectangle: Invalid int3D")
                }
                data3DToRectangleFunction[int3D.x][int3D.y][int3D.z](mapId, id)
            }

        val positionToInt3D: (Int, Int) -> Int3D = { xPos, yPos ->
            val x: Int = (xPos - xOffSet) / xyFullSpace
            val y: Int = (yPos - yOffSet) / xyFullSpace
            val z: Int = ((xPos - xOffSet) % xyFullSpace) / xSingleSpace
            Int3D(x, y, z)
        }

        val positionToId: (Int, Int) -> Int = { xPos, yPos ->
            val int3D: Int3D = positionToInt3D(xPos, yPos)
            if (isInt3DValid(int3D, data3D)) {
                if (int3DToRectangle(int3D).contains(xPos, yPos)) {
                    positionToIdAtData3DFunction[int3D.x][int3D.y][int3D.z](xPos, yPos)
                } else {
                    -1
                }
            } else {
                -1
            }
        }

        return Data3DProjectionFunction(
            zBegin = 0,
            zEnd = zDim - 1,
            int3DToRectangle = int3DToRectangle,
            data3DToRectangle = data3DToRectangle,
            positionToInt3D = positionToInt3D,
            positionToId = positionToId,
        )
    }

    /**
     * Function for projecting data3D, with limited z dimension
     *
     * @param data3D universe 3d view data, should be a cuboid
     * @param center the Int3D coordinate which the z limit spans around
     * @param zLimit limit of z dimension
     * @param imageWidth height of the texture image
     * @param imageHeight width of the texture image
     * @param gridXSeparation the unscaled spacing in x axis between grid of different z coordinate
     * @param gridYSeparation the unscaled spacing in Y axis between grid of different z coordinate
     * @param xPadding width padding around the whole projected plane
     * @param yPadding height padding around the whole projected plane
     *
     * @return function for calculating the projection
     */
    fun createData3D2DProjection(
        data3D: List<List<List<Map<Int, List<Int>>>>>,
        center: Int3D,
        zLimit: Int,
        imageWidth: Int,
        imageHeight: Int,
        gridXSeparation: Int,
        gridYSeparation: Int,
        xPadding: Int,
        yPadding: Int,
    ): Data3D2DProjection {
        val xDim: Int = data3D.size
        val yDim: Int = data3D[center.x].size
        val zDim: Int = data3D[center.x][center.y].size

        val xBegin: Int = 0
        val xEnd: Int = xDim - 1
        val yBegin = 0
        val yEnd = yDim - 1
        val zBegin: Int = max(0, center.z - zLimit / 2)
        val zEnd: Int = min(zDim - 1, zBegin + zLimit - 1)

        val data3DWithZLimit: List<List<List<Map<Int, List<Int>>>>> = data3D.map { yList ->
            yList.map { zList ->
                zList.slice(zBegin..zEnd)
            }
        }

        val data3DProjectionFunction: Data3DProjectionFunction = createData3DProjectionFunction(
            data3DWithZLimit,
            imageWidth,
            imageHeight,
            gridXSeparation,
            gridYSeparation,
            xPadding,
            yPadding,
        )

        val int3DToRectangle: (Int3D) -> IntRectangle = { int3D ->
            val int3DWithZLimit = Int3D(int3D.x, int3D.y, int3D.z - zBegin)
            data3DProjectionFunction.int3DToRectangle(int3DWithZLimit)
        }

        val data3DToRectangle: (Int3D, Int, Int) -> IntRectangle = { int3D, mapId, id ->
            val int3DWithZLimit = Int3D(int3D.x, int3D.y, int3D.z - zBegin)
            data3DProjectionFunction.data3DToRectangle(int3DWithZLimit, mapId, id)
        }

        val positionToInt3D: (Int, Int) -> Int3D = { xPos, yPos ->
            val int3DWithZLimit = data3DProjectionFunction.positionToInt3D(xPos, yPos)
            Int3D(int3DWithZLimit.x, int3DWithZLimit.y, int3DWithZLimit.z + zBegin)
        }

        val positionToId: (Int, Int) -> Int = { xPos, yPos ->
            data3DProjectionFunction.positionToId(xPos, yPos)
        }

        val lastInt3DRectangle: IntRectangle = int3DToRectangle(Int3D(xEnd, yEnd, zEnd))

        return Data3D2DProjection(
            xBegin = xBegin,
            xEnd = xEnd,
            yBegin = yBegin,
            yEnd = yEnd,
            zBegin = zBegin,
            zEnd = zEnd,
            width = lastInt3DRectangle.xPos + lastInt3DRectangle.width + 2 * xPadding,
            height = lastInt3DRectangle.yPos + lastInt3DRectangle.height + 2 * yPadding,
            idList = data3DWithZLimit.flatten().flatten().flatMap { it.values }.flatten(),
            int3DToRectangle = int3DToRectangle,
            data3DToRectangle = data3DToRectangle,
            positionToInt3D = positionToInt3D,
            positionToId = positionToId,
        )
    }


    private val logger = RelativitizationLogManager.getLogger()
}

/**
 * Store function related to data 3D projection (and reverse projection)
 *
 * @property zBegin beginning of acceptable z coordinate
 * @property zEnd end of acceptable z coordinate
 * @property int3DToRectangle from int3D to Rectangle of the grid
 * @property data3DToRectangle from int3D, mapId, id to Rectangle of the object
 * @property positionToInt3D from posX, posY to int3D of the grid
 * @property positionToId from posX, posY to id of the object
 */
data class Data3DProjectionFunction(
    val zBegin: Int,
    val zEnd: Int,
    val int3DToRectangle: (Int3D) -> IntRectangle,
    val data3DToRectangle: (Int3D, Int, Int) -> IntRectangle,
    val positionToInt3D: (Int, Int) -> Int3D,
    val positionToId: (Int, Int) -> Int,
)

/**
 * The ultimate data class handling 3d to 2d projection
 *
 * @property xBegin beginning of acceptable x coordinate
 * @property xEnd end of acceptable x coordinate
 * @property yBegin beginning of acceptable y coordinate
 * @property yEnd end of acceptable y coordinate
 * @property zBegin beginning of acceptable z coordinate
 * @property zEnd end of acceptable z coordinate
 * @property width width of the whole projected 2d plane
 * @property height height of the whole projected 2d plane
 * @property int3DToRectangle from int3D to Rectangle of the grid
 * @property data3DToRectangle from int3D, mapId, id to Rectangle of the object
 * @property positionToInt3D from posX, posY to int3D of the grid
 * @property positionToId from posX, posY to id of the object
 */
data class Data3D2DProjection(
    val xBegin: Int,
    val xEnd: Int,
    val yBegin: Int,
    val yEnd: Int,
    val zBegin: Int,
    val zEnd: Int,
    val width: Int,
    val height: Int,
    val idList: List<Int>,
    val int3DToRectangle: (Int3D) -> IntRectangle,
    val data3DToRectangle: (Int3D, Int, Int) -> IntRectangle,
    val positionToInt3D: (Int, Int) -> Int3D,
    val positionToId: (Int, Int) -> Int,
)