package relativitization.universe.maths.grid

import relativitization.universe.data.physics.Int3D
import relativitization.universe.data.physics.MutableDouble4D

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
        List(xDim) { create2DGrid<T>(yDim, zDim) { y, z -> generate(it, y, z)} }


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
        MutableList(tDim) { create3DGrid<T>(xDim, yDim, zDim) { x, y, z -> generate(it, x, y, z)} }

    /**
     * Whether the coordinate belong to same cube in a grid
     */
    fun sameCube(md1: MutableDouble4D, md2: MutableDouble4D, edgeLength: Double): Boolean {
        return if (md1.toMutableInt4D() != md2.toMutableInt4D()) {
            false
        } else {
            val xExtra1: Double = md1.x - md1.x.toInt()
            val xExtra2: Double = md2.x - md2.x.toInt()
            val yExtra1: Double = md1.y - md1.y.toInt()
            val yExtra2: Double = md2.y - md2.y.toInt()
            val zExtra1: Double = md1.z - md1.z.toInt()
            val zExtra2: Double = md2.z - md2.z.toInt()

            val xNum1 = (xExtra1 / edgeLength).toInt()
            val xNum2 = (xExtra2 / edgeLength).toInt()
            val yNum1 = (yExtra1 / edgeLength).toInt()
            val yNum2 = (yExtra2 / edgeLength).toInt()
            val zNum1 = (zExtra1 / edgeLength).toInt()
            val zNum2 = (zExtra2 / edgeLength).toInt()

            (xNum1 == xNum2) && (yNum1 == yNum2) && (zNum1 == zNum2)
        }
    }

    /**
     * Number of edges in a cube when dividing a int4D cube into several cube to place double4D
     */
    fun numEdge(edgeLength: Double): Int = (1.0 / edgeLength).toInt() + 1

    /**
     * Compute the id of the cube the double4D belongs to
     */
    fun double4DToId(mutableDouble4D: MutableDouble4D, edgeLength: Double): Int {
        val numEdge: Int = numEdge(edgeLength)

        val xExtra: Double = mutableDouble4D.x - mutableDouble4D.x.toInt()
        val yExtra: Double = mutableDouble4D.y - mutableDouble4D.y.toInt()
        val zExtra: Double = mutableDouble4D.z - mutableDouble4D.z.toInt()

        val xNum = (xExtra / edgeLength).toInt()
        val yNum = (yExtra / edgeLength).toInt()
        val zNum = (zExtra / edgeLength).toInt()

        return xNum * numEdge * numEdge + yNum * numEdge + zNum
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