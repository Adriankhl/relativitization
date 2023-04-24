package relativitization.universe.maths.grid

import relativitization.universe.maths.grid.Projections.idAtGridToRectangleFunction
import relativitization.universe.maths.grid.Projections.indexToRectangleFunction
import relativitization.universe.maths.grid.Projections.positionToIdAtGridFunction
import relativitization.universe.maths.grid.Projections.positionToIndexFunction
import kotlin.test.Test

internal class ProjectionsTest {
    @Test
    fun indexToRectangleTest() {
        val iFunc = indexToRectangleFunction(
            numRequiredRectangle = 10,
            imageHeight = 512,
            imageWidth = 512,
            groupHeight = 4096,
            groupWidth = 4096,
            xOffSet = 100,
            yOffSet = 200
        )

        assert(iFunc(2) == IntRectangle(xPos = 2148, yPos = 200, width = 1024, height = 1024))
        assert(iFunc(5) == IntRectangle(xPos = 1124, yPos = 1224, width = 1024, height = 1024))
    }

    @Test
    fun positionToIndexTest() {
        val pFunc = positionToIndexFunction(
            numRequiredRectangle = 10,
            imageHeight = 512,
            imageWidth = 512,
            groupHeight = 4096,
            groupWidth = 4096,
            xOffSet = 100,
            yOffSet = 200
        )

        assert(pFunc(2148, 1300) == 6)
    }


    @Test
    fun idAtGridToRectangleTest() {
        val gridMap = mapOf(3 to listOf(1, 3), 2 to listOf(2, 4, 5, 6))
        val iFunc = idAtGridToRectangleFunction(
            gridMap = gridMap,
            imageHeight = 512,
            imageWidth = 512,
            gridHeight = 4096,
            gridWidth = 4096,
            xOffSet = 100,
            yOffSet = 200
        )

        assert(iFunc(3, 1) == IntRectangle(xPos = 2148, yPos = 2248, width = 1024, height = 1024))
    }


    @Test
    fun positionToIdAtGridTest() {
        val gridMap = mapOf(3 to listOf(1, 3), 2 to listOf(2, 4, 5, 6))
        val pFunc = positionToIdAtGridFunction(
            gridMap = gridMap,
            imageHeight = 512,
            imageWidth = 512,
            gridHeight = 4096,
            gridWidth = 4096,
            xOffSet = 100,
            yOffSet = 200
        )

        assert(pFunc(2200, 2300) == 1)
    }
}