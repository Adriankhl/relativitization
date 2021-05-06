package relativitization.universe.maths.grid

import kotlin.test.Test
import relativitization.universe.data.physics.Int2D
import relativitization.universe.maths.grid.Projections.coordinate2DFrom3D
import relativitization.universe.maths.grid.Projections.indexToRectangleFunction
import relativitization.universe.maths.grid.Projections.positionToIndexFunction

internal class ProjectionsTest {
    @Test
    fun indexToRectangleTest() {
        val iFunc = indexToRectangleFunction(
            numRequiredRectangle = 10,
            groupHeight = 4096,
            groupWidth = 4096,
            imageHeight = 512,
            imageWidth = 512,
            xOffSet = 100,
            yOffSet = 200
        )

        assert(iFunc(2) == IntRectangle(xPos=2148, yPos=200, width=1024, height=1024))
        assert(iFunc(5) == IntRectangle(xPos=1124, yPos=1224, width=1024, height=1024))
    }

    @Test
    fun positionToIndexTest() {
        val pFunc = positionToIndexFunction(
            numRequiredRectangle = 10,
            groupHeight = 4096,
            groupWidth = 4096,
            imageHeight = 512,
            imageWidth = 512,
            xOffSet = 100,
            yOffSet = 200
        )

        assert(pFunc(2148, 1300) == 6)
    }
}