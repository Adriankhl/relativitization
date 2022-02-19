package relativitization.universe.data.components.defaults.physics

import relativitization.universe.maths.grid.Grids
import relativitization.universe.maths.physics.Int3D
import kotlin.test.Test

internal class Int3DTest {
    @Test
    fun surfaceTest() {
        val i1 = Int3D(1, 1, 1)
        val l1: List<Int3D> = Grids.create3DGrid(3, 3, 3) { x, y, z ->
            Int3D(x, y, z)
        }.flatten().flatten() - i1

        val il1 = i1.getInt3DSurfaceList(
            halfEdgeLength = 1,
            minX = 0,
            maxX = 2,
            minY = 0,
            maxY = 2,
            minZ = 0,
            maxZ = 2
        )

        assert(l1.all { il1.contains(it) } && il1.all { l1.contains(it) })
    }

    @Test
    fun duplicateTest() {
        val l = listOf(Int3D(1, 1, 1), Int3D(1, 1, 1))
        val s = l.toSet()

        assert(s == setOf(Int3D(1, 1, 1)))
    }
}