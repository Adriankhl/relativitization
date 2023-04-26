package relativitization.universe.game.data.components

import relativitization.universe.core.data.components.PlayerDataComponentMap
import relativitization.universe.core.data.serializer.DataSerializer
import kotlin.test.Test

internal class PlayerDataComponentMapTest {
    @Test
    fun nullTest() {
        val d1: DiplomacyData? = null
        assert(d1 !is DiplomacyData)
    }

    @Test
    fun getOrDefaultTest() {
        val d = PlayerDataComponentMap(
            listOf(DataSerializer.copy<_, AIData>(MutableAIData(AITask.EMPTY)))
        )
        assert(
            d.getOrDefault(DataSerializer.copy<_, AIData>(MutableAIData())) ==
                    DataSerializer.copy<_, AIData>(MutableAIData(AITask.EMPTY))
        )
    }

    @Test
    fun getTest() {
        val d = PlayerDataComponentMap(
            listOf(DataSerializer.copy<_, AIData>(MutableAIData(AITask.EMPTY)))
        )
        assert(
            d.get<AIData>() ==
                    DataSerializer.copy<_, AIData>(MutableAIData(AITask.EMPTY))
        )
    }
}