package relativitization.universe.game.data.components

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
            listOf(AIData(AITask.EMPTY))
        )
        assert(d.getOrDefault(AIData()) == AIData(AITask.EMPTY))
    }

    @Test
    fun getTest() {
        val d = PlayerDataComponentMap(
            listOf(AIData(AITask.EMPTY))
        )
        assert(d.get<AIData>() == AIData(AITask.EMPTY))
    }
}