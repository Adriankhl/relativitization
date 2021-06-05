package relativitization.universe.maths.grid

import kotlin.test.Test
import relativitization.universe.data.physics.Int3D
import relativitization.universe.data.physics.MutableDouble4D

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

    @Test
    fun double4DToIdTest() {
        val id1 = Grids.maxDouble4DtoId(0.01000001)
        val id2 = Grids.double4DToId(MutableDouble4D(0.999, 0.999, 0.999, 0.999), 0.01000001)

        println(id1)
        println(id2)

        assert(id1 == id2)
    }
}