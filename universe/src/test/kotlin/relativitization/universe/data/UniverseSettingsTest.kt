package relativitization.universe.data

import relativitization.universe.data.serializer.DataSerializer
import kotlin.test.Test

internal class UniverseSettingsTest {
    @Test
    fun serializationTest() {
        val mutableUniverseSettings = MutableUniverseSettings()
        val universeSettings: UniverseSettings = DataSerializer.copy(mutableUniverseSettings)
        val m2: MutableUniverseSettings = DataSerializer.copy(universeSettings)
        assert(m2 == mutableUniverseSettings)
    }
}