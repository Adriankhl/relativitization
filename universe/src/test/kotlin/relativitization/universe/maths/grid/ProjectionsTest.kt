package relativitization.universe.maths.grid

import org.junit.jupiter.api.Test
import relativitization.universe.data.physics.Int2D
import relativitization.universe.maths.grid.Projections.coordinate2DFrom3D

internal class ProjectionsTest {
    @Test
    fun test2DFrom3D() {
        val v = coordinate2DFrom3D(
            1,
            1,
            1,
            2,
            10,
            10,
            2,
            1,
            1,
            1,
        )
        assert(v == Int2D(120, 110))
    }

}