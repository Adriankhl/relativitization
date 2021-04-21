package relativitization.universe.data

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import relativitization.universe.data.serializer.DataSerializer.copy

internal class UniverseSettingsTest {
    @Test
    fun serialization() {
        val mutableUniverseSettings = MutableUniverseSettings()
        val universeSettings: UniverseSettings = copy(mutableUniverseSettings)
        val m2: MutableUniverseSettings = copy(universeSettings)
        assert(m2 == mutableUniverseSettings)
    }
}