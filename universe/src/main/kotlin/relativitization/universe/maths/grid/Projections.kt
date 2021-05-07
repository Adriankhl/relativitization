package relativitization.universe.maths.grid

import kotlinx.serialization.descriptors.PrimitiveKind
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
    private fun gridScaleFactor(
        data3D: List<List<List<Map<Int, List<Int>>>>>,
    ): Int {
        return data3D.flatten().flatten().map { gridMap ->
            // multiply by two to insert spacing
            val gridDivision: Int = numDivisionInGroup(gridMap.size * 2 )
            val maxInnerDivision: Int = gridMap.values.map { idList ->
                numDivisionInGroup(idList.size)
            }.maxOrNull() ?: 1
            maxInnerDivision * gridDivision
        }.maxOrNull() ?: 1
    }

    /**
     * Function
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
    fun data3DProjectionFunction(
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

        val data3DToRectangleFunction: List<List<List<(Int, Int) -> IntRectangle>>> = int3DRectangleData.mapIndexed { x, yList ->
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

        val positionToIdAtData3DFunction: List<List<List<(Int, Int) -> Int>>> = int3DRectangleData.mapIndexed { x, yList ->
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
            if (isInt3DValid(int3D, data3D)) {
                logger.error("int3DToRectangle: Invalid int3D")
            }
            int3DRectangleData[int3D.x][int3D.y][int3D.z]
        }

        val data3DToRectangle: (Int3D, Int, Int) -> IntRectangle = { int3D: Int3D, mapId: Int, id:Int ->
            if (isInt3DValid(int3D, data3D)) {
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
            int3DToRectangle = int3DToRectangle,
            data3DToRectangle = data3DToRectangle,
            positionToInt3D = positionToInt3D,
            positionToId = positionToId,
        )
    }


    private val logger = LogManager.getLogger()
}

/**
 * Store function related to data 3D projection
 *
 * @property int3DToRectangle from int3D to Rectangle of the grid
 * @property data3DToRectangle from int3D, mapId, id to Rectangle of the object
 * @property positionToInt3D from posX, posY to int3D of the grid
 * @property positionToId from posX, posY to id of the object
 */
data class Data3DProjectionFunction(
    val int3DToRectangle: (Int3D) -> IntRectangle,
    val data3DToRectangle: (Int3D, Int, Int) -> IntRectangle,
    val positionToInt3D: (Int, Int) -> Int3D,
    val positionToId: (Int, Int) -> Int,
)