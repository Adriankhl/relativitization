package relativitization.universe.data.global.components

import relativitization.universe.data.serializer.DataSerializer
import kotlin.test.Test

internal class UniverseScienceDataTest {
    @Test
    fun serializationTest() {
        val u1: MutableUniverseScienceData = DataSerializer.copy(UniverseScienceData())
        val u2: UniverseScienceData = DataSerializer.copy(u1)
        assert(u2 == UniverseScienceData())
    }
}