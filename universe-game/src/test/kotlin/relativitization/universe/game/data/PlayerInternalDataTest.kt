package relativitization.universe.game.data

import relativitization.universe.game.data.components.EconomyData
import relativitization.universe.game.data.components.MutableEconomyData
import relativitization.universe.game.data.components.MutablePhysicsData
import relativitization.universe.game.data.components.MutablePlayerDataComponent
import relativitization.universe.game.data.components.PhysicsData
import relativitization.universe.game.data.components.PlayerDataComponent
import relativitization.universe.game.data.serializer.DataSerializer
import kotlin.test.Test

internal class PlayerInternalDataTest {
    @Test
    fun serialization() {
        val l1: List<PlayerDataComponent> = listOf(PhysicsData(), EconomyData())
        val l2: List<PlayerDataComponent> = DataSerializer.copy(l1)
        assert(l1 == l2)

        val l3: List<MutablePlayerDataComponent> =
            listOf(MutablePhysicsData(), MutableEconomyData())
        val l4: List<MutablePlayerDataComponent> = DataSerializer.copy(l3)
        assert(l3 == l4)

        //println(DataSerializer.encode(l3))

        val l5: List<MutablePlayerDataComponent> = DataSerializer.copy(l2)
        assert(l3 == l5)

        val l6: List<PlayerDataComponent> = DataSerializer.copy(l3)
        assert(l1 == l6)
    }
}