package relativitization.universe.data

import kotlin.test.Test
import relativitization.universe.data.serializer.DataSerializer.copy

internal class UniverseSettingsTest {
    @Test
    fun serializationTest() {
        val mutableUniverseSettings = MutableUniverseSettings()
        val universeSettings: UniverseSettings = copy(mutableUniverseSettings)
        val m2: MutableUniverseSettings = copy(universeSettings)
        assert(m2 == mutableUniverseSettings)
    }
}