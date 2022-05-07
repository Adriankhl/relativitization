package relativitization.universe

import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.UniverseData
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.name
import relativitization.universe.generate.testing.TestingFixedMinimal
import kotlin.test.Test

internal class UniverseTest {
    @Test
    fun saveLoadTest() {
        val generateSetting = GenerateSettings(
            generateMethod = TestingFixedMinimal.name(),
            numPlayer = 4,
            numHumanPlayer = 2,
            otherIntMap = mutableMapOf("numExtraStellarSystem" to 3),
            otherDoubleMap = mutableMapOf("initialPopulation" to 1E6),
            universeSettings = MutableUniverseSettings(universeName = "save-load-test")
        )
        val universeData: UniverseData = GenerateUniverseMethodCollection.generate(generateSetting)
        val universe = Universe(universeData)
        universe.saveAll()
        val universeLoad = Universe.loadUniverseLatest("save-load-test", ".")
        println(universeLoad)
    }
}