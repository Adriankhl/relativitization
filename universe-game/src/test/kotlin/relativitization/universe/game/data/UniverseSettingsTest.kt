package relativitization.universe.game.data

import relativitization.universe.core.data.MutableUniverseSettings
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.serializer.DataSerializer
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