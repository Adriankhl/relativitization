package relativitization.universe.data.component.economy

import kotlin.test.Test
import kotlin.test.assertNotEquals

internal class ResourceQualityClassTest {
    @Test
    fun orderTest() {
        val a: List<ResourceQualityClass> = ResourceQualityClass.values().toList()
        val b = a.map { it == ResourceQualityClass.FIRST }
        assert(b[0])
        assert(!b[1] && !b[2])
    }
}