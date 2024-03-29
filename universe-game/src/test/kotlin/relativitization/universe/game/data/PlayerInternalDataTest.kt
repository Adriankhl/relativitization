package relativitization.universe.game.data

import relativitization.universe.core.data.components.MutablePlayerDataComponent
import relativitization.universe.core.data.components.PlayerDataComponent
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.game.GameUniverseInitializer
import relativitization.universe.game.data.components.EconomyData
import relativitization.universe.game.data.components.MutableEconomyData
import relativitization.universe.game.data.components.MutablePhysicsData
import relativitization.universe.game.data.components.PhysicsData
import kotlin.test.Test

internal class PlayerInternalDataTest {
    @Test
    fun serialization() {
        GameUniverseInitializer.initialize()

        val l1: List<PlayerDataComponent> = listOf(
            DataSerializer.copy<_, PhysicsData>(MutablePhysicsData()),
            DataSerializer.copy<_, EconomyData>(MutableEconomyData()),
        )
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