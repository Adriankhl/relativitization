package relativitization.universe.core.generate.empty

import relativitization.universe.core.Universe
import relativitization.universe.core.data.MutableUniverseSettings
import relativitization.universe.core.data.UniverseData
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethodCollection
import kotlin.test.Test

internal class EmptyUniverseTest {
    @Test
    fun randomSeedTest() {
        val generateSetting = GenerateSettings(
            universeSettings = MutableUniverseSettings()
        )

        val universeData1: UniverseData = GenerateUniverseMethodCollection.generate(generateSetting)
        val universeData2: UniverseData = GenerateUniverseMethodCollection.generate(generateSetting)

        val universe1 = Universe(universeData1)
        val universe2 = Universe(universeData2)

        assert(
            universe1.getUniverse3DViewAtPlayer(1) == universe2.getUniverse3DViewAtPlayer(1)
        )

        for (step in 1..20) {
            universe1.pureAIStep()
            universe2.pureAIStep()
        }

        assert(
            universe1.getUniverse3DViewAtPlayer(1) == universe2.getUniverse3DViewAtPlayer(1)
        )
    }
}