package relativitization.universe.data

import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.data.subsystem.*
import kotlin.test.Test

internal class PlayerInternalDataTest {
    @Test
    fun serialization() {
        val l1: List<PlayerSubsystemData> = listOf(PhysicsData(), EconomyData())
        val l2: List<PlayerSubsystemData> = DataSerializer.copy(l1)
        assert(l1 == l2)

        val l3: List<MutablePlayerSubsystemData> = listOf(MutablePhysicsData(), MutableEconomyData())
        val l4: List<MutablePlayerSubsystemData> = DataSerializer.copy(l3)
        assert(l3 == l4)

        println(DataSerializer.encode(l3))

        val l5: List<MutablePlayerSubsystemData> = DataSerializer.copy(l2)
        assert(l3 == l5)
    }
}