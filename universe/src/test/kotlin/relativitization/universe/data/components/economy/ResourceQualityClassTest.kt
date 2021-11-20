package relativitization.universe.data.components.economy

import relativitization.universe.data.components.default.economy.ResourceQualityClass
import kotlin.test.Test

internal class ResourceQualityClassTest {
    @Test
    fun orderTest() {
        val a: List<ResourceQualityClass> = ResourceQualityClass.values().toList()
        val b = a.map { it == ResourceQualityClass.FIRST }
        assert(b[0])
        assert(!b[1] && !b[2])
    }
}