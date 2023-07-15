package relativitization.universe.game.data.components.defaults.economy

import kotlin.test.Test

internal class ResourceQualityClassTest {
    @Test
    fun orderTest() {
        val a: List<ResourceQualityClass> = ResourceQualityClass.entries
        val b = a.map { it == ResourceQualityClass.FIRST }
        assert(b[0])
        assert(!b[1] && !b[2])
    }
}