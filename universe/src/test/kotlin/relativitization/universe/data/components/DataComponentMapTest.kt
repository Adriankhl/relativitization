package relativitization.universe.data.components

import kotlin.test.Test

internal class DataComponentMapTest {
    @Test
    fun nullTest() {
        val d1: DiplomacyData? = null
        assert(d1 !is DiplomacyData)

        val d2: DiplomacyData? = DiplomacyData()
        assert(d2 is DiplomacyData)
    }

    @Test
    fun getOrDefaultTest() {
        val d = DataComponentMap(
            listOf(AIData("Hello"))
        )
        assert(d.getOrDefault(AIData::class, AIData()) == AIData("Hello"))
    }
}