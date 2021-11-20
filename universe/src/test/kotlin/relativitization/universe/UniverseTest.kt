package relativitization.universe

import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.UniverseGenerateMethodCollection
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
        val universeData = UniverseGenerateMethodCollection.generate(generateSetting)
        val universe = Universe(universeData = universeData, programDir= ".")
        universe.saveAll()
        val universeLoad = Universe.loadUniverseLatest("save-load-test", ".")
        println(universeLoad)
    }
}