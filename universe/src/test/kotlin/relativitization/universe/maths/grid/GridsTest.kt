package relativitization.universe.maths.grid

import relativitization.universe.data.component.physics.Double3D
import relativitization.universe.data.component.physics.Int3D
import relativitization.universe.data.component.physics.MutableDouble4D
import kotlin.test.Test

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
        val id1 = Grids.maxDouble4DtoId(0.01)
        val id2 = Grids.double4DToGroupId(MutableDouble4D(0.999, 0.999, 0.999, 0.999), 0.01)

        assert(id1 == id2)

        val id3 = Grids.double4DToGroupId(MutableDouble4D(1.0, 0.0, 0.1, 0.0), 0.01)
        assert(id3 == 1000)
    }

    @Test
    fun idToDouble3DTest() {
        val id1 = Grids.double4DToGroupId(MutableDouble4D(1.0, 0.121, 0.459, 0.872), 0.01)
        val do1 = Grids.groupIdToCenterDouble3D(id1, 0.01)

        assert(do1 == Double3D(0.125, 0.455, 0.875))

        val id2 = Grids.double4DToGroupId(MutableDouble4D(1.0, 1.0, 0.0, 0.0), 0.01)
        val do2 = Grids.groupIdToCenterDouble3D(id2, 0.01)

        assert(do2 == Double3D(0.005, 0.005, 0.005))
    }
}