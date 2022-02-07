package relativitization.universe.maths.grid

import relativitization.universe.data.components.defaults.physics.Double3D
import relativitization.universe.data.components.defaults.physics.Double4D
import relativitization.universe.data.components.defaults.physics.Int3D
import relativitization.universe.data.components.defaults.physics.MutableDouble4D
import kotlin.math.floor

object Grids {

    /**
     * Create 1 dimensional array of T
     *
     * @param T type of the object in the grid
     * @param xDim x dimension of the grid
     * @param generate a function to generate object of type T as a function of x coordinate
     */
    fun <T> create1DGrid(xDim: Int, generate: (Int) -> T): List<T> =
        List(xDim) { generate(it) }

    /**
     * Create 2 dimensional array of T
     *
     * @param T type of the object in the grid
     * @param xDim x dimension of the grid
     * @param yDim y dimension of the grid
     * @param generate a function to generate object of type T as a function of x, y coordinate
     */
    fun <T> create2DGrid(xDim: Int, yDim: Int, generate: (Int, Int) -> T): List<List<T>> =
        List(xDim) { create1DGrid<T>(yDim) { y -> generate(it, y) } }

    /**
     * Create 3 dimensional array of T
     *
     * @param T type of the object in the grid
     * @param xDim x dimension of the grid
     * @param yDim y dimension of the grid
     * @param zDim z dimension of the grid
     * @param generate a function to generate object of type T as a function of x, y, z coordinate
     */
    fun <T> create3DGrid(
        xDim: Int,
        yDim: Int,
        zDim: Int,
        generate: (Int, Int, Int) -> T
    ): List<List<List<T>>> =
        List(xDim) { create2DGrid<T>(yDim, zDim) { y, z -> generate(it, y, z) } }


    /**
     * Create 4 dimensional array of T, the t dimension list is mutable
     *
     * @param T type of the object in the grid
     * @param tDim t dimension of the grid
     * @param xDim x dimension of the grid
     * @param yDim y dimension of the grid
     * @param zDim z dimension of the grid
     * @param generate a function to generate object of type T as a function of t, x, y, z coordinate
     */
    fun <T> create4DGrid(
        tDim: Int,
        xDim: Int,
        yDim: Int,
        zDim: Int,
        generate: (Int, Int, Int, Int) -> T
    ): MutableList<List<List<List<T>>>> =
        MutableList(tDim) { create3DGrid<T>(xDim, yDim, zDim) { x, y, z -> generate(it, x, y, z) } }

    /**
     * Whether the coordinate belong to same group in a cube
     */
    fun sameGroup(md1: MutableDouble4D, md2: MutableDouble4D, edgeLength: Double): Boolean {
        return if (md1.toMutableInt4D() != md2.toMutableInt4D()) {
            false
        } else {
            val xExtra1: Double = md1.x - floor(md1.x)
            val xExtra2: Double = md2.x - floor(md2.x)
            val yExtra1: Double = md1.y - floor(md1.y)
            val yExtra2: Double = md2.y - floor(md2.y)
            val zExtra1: Double = md1.z - floor(md1.z)
            val zExtra2: Double = md2.z - floor(md2.z)

            val xNum1: Int = floor(xExtra1 / edgeLength).toInt()
            val xNum2: Int = floor(xExtra2 / edgeLength).toInt()
            val yNum1: Int = floor(yExtra1 / edgeLength).toInt()
            val yNum2: Int = floor(yExtra2 / edgeLength).toInt()
            val zNum1: Int = floor(zExtra1 / edgeLength).toInt()
            val zNum2: Int = floor(zExtra2 / edgeLength).toInt()

            (xNum1 == xNum2) && (yNum1 == yNum2) && (zNum1 == zNum2)
        }
    }

    /**
     * Number of edges (rounded up) in a cube when dividing a int4D cube into several cube to place double4D
     */
    fun numEdge(edgeLength: Double): Int {
        val num: Double = (1.0 / edgeLength)
        return if (num % 1.0 == 0.0) {
            floor(num).toInt()
        } else {
            floor(num).toInt() + 1
        }
    }

    /**
     * Compute the id of the cube the double4D belongs to group id
     */
    fun double4DToGroupId(mutableDouble4D: MutableDouble4D, edgeLength: Double): Int {
        val numEdge: Int = numEdge(edgeLength)

        val xExtra: Double = mutableDouble4D.x - floor(mutableDouble4D.x)
        val yExtra: Double = mutableDouble4D.y - floor(mutableDouble4D.y)
        val zExtra: Double = mutableDouble4D.z - floor(mutableDouble4D.z)

        val xNum: Int = floor(xExtra / edgeLength).toInt()
        val yNum: Int = floor(yExtra / edgeLength).toInt()
        val zNum: Int = floor(zExtra / edgeLength).toInt()

        return xNum * numEdge * numEdge + yNum * numEdge + zNum
    }

    /**
     * Compute the id of the cube the double4D belongs to group id
     */
    fun double4DToGroupId(double4D: Double4D, edgeLength: Double): Int {
        val numEdge: Int = numEdge(edgeLength)

        val xExtra: Double = double4D.x - floor(double4D.x)
        val yExtra: Double = double4D.y - floor(double4D.y)
        val zExtra: Double = double4D.z - floor(double4D.z)

        val xNum = floor(xExtra / edgeLength).toInt()
        val yNum = floor(yExtra / edgeLength).toInt()
        val zNum = floor(zExtra / edgeLength).toInt()

        return xNum * numEdge * numEdge + yNum * numEdge + zNum
    }

    /**
     * Compute the center of double 3D from group id
     *
     * @return the double3D is within the square (1.0, 1.0, 1.0), so extra vector has to be added to point to
     * the actual position
     */
    fun groupIdToCenterDouble3D(groupId: Int, edgeLength: Double): Double3D {
        val numEdge: Int = numEdge(edgeLength)

        val x: Double = (groupId / numEdge / numEdge).toDouble() * edgeLength + edgeLength * 0.5
        val y: Double =
            ((groupId % (numEdge * numEdge)) / numEdge).toDouble() * edgeLength + edgeLength * 0.5
        val z: Double = groupId % numEdge * edgeLength + edgeLength * 0.5

        return Double3D(x, y, z)
    }

    /**
     * The maximum id of the double 4D
     */
    fun maxDouble4DtoId(edgeLength: Double): Int {
        val numEdge = numEdge(edgeLength)
        return numEdge * numEdge * numEdge - 1
    }

    /**
     * Check if int3d is valid
     */
    fun <T> isInt3DValid(int3D: Int3D, data3D: List<List<List<T>>>): Boolean {
        return ((int3D.x >= 0) && (int3D.x < data3D.size) &&
                (int3D.y >= 0) && (int3D.y < data3D[int3D.x].size) &&
                (int3D.z >= 0) && (int3D.z < data3D[int3D.x][int3D.y].size))
    }
}

/**
 * Rectangle with integer parameters
 */
data class IntRectangle(val xPos: Int, val yPos: Int, val width: Int, val height: Int) {
    fun contains(x: Int, y: Int): Boolean {
        return (x >= xPos) && (x < xPos + width) && (y >= yPos) && (y < yPos + height)
    }

    fun centerX(): Float = xPos.toFloat() + width.toFloat() / 2
    fun centerY(): Float = yPos.toFloat() + height.toFloat() / 2
}