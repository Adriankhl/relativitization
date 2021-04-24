package relativitization.universe.maths.grid

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import relativitization.universe.data.physics.Int3D

internal class GridsTest {
    @Test
    fun list3DTest() {
        val list3D = Grids.create3DGrid(2, 2, 2) { x, y, z ->
            Int3D(x, y, z)
        }.flatten().flatten()

        val testList = listOf(
            Int3D(x=0, y=0, z=0),
            Int3D(x=0, y=0, z=1),
            Int3D(x=0, y=1, z=0),
            Int3D(x=0, y=1, z=1),
            Int3D(x=1, y=0, z=0),
            Int3D(x=1, y=0, z=1),
            Int3D(x=1, y=1, z=0),
            Int3D(x=1, y=1, z=1)
        )

        assert(list3D == testList)
    }
}