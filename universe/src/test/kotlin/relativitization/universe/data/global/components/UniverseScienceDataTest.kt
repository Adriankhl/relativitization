package relativitization.universe.data.global.components

import relativitization.universe.data.serializer.DataSerializer
import kotlin.test.Test

internal class UniverseScienceDataTest {
    @Test
    fun serializationTest() {
        println(DataSerializer.encode(UniverseScienceData()))
        val u1: MutableUniverseScienceData = DataSerializer.copy(UniverseScienceData())
        val u2: UniverseScienceData = DataSerializer.copy(MutableUniverseScienceData())
    }
}