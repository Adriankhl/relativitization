package relativitization.universe

import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverse
import kotlin.test.Test

internal class UniverseTest {
    @Test
    fun saveLoadTest() {
        val generateSetting = GenerateSettings(
            generateMethod = "fixed-Minimal",
            numPlayer = 4,
            numHumanPlayer = 2,
            numExtraStellarSystem = 3,
            universeSettings = MutableUniverseSettings(universeName = "save-load-test")
        )
        val universeData = GenerateUniverse.generate(generateSetting)
        val universe = Universe(universeData = universeData, saveDirPath = ".")
        universe.saveAll()
        val universeLoad = Universe.loadUniverseLatest("save-load-test", ".")
    }
}