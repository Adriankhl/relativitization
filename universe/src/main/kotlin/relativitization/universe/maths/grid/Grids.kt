package relativitization.universe.maths.grid

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
}

/**
 * Rectangle with integer parameters
 */
data class IntRectangle(val xPos: Int, val yPos: Int, val width: Int, val height: Int)