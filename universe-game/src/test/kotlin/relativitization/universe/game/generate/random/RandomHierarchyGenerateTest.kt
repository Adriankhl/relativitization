package relativitization.universe.game.generate.random

import org.junit.jupiter.api.Test
import relativitization.universe.game.Universe
import relativitization.universe.game.data.MutableUniverseSettings
import relativitization.universe.game.data.UniverseData
import relativitization.universe.game.data.commands.DefaultCommandAvailability
import relativitization.universe.game.generate.GenerateSettings
import relativitization.universe.game.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.global.DefaultGlobalMechanismList
import relativitization.universe.game.mechanisms.DefaultMechanismLists

internal class RandomHierarchyGenerateTest {
    @Test
    fun randomSeedTest() {
        // Set the initial population to 1E8 to test how the AI do with a big population
        val generateSetting = GenerateSettings(
            generateMethod = RandomHierarchyGenerate.name(),
            numPlayer = 100,
            numHumanPlayer = 8,
            otherDoubleMap = mutableMapOf("initialPopulation" to 1E6),
            universeSettings = MutableUniverseSettings(
                universeName = "Random seed test",
                commandCollectionName = DefaultCommandAvailability.name(),
                mechanismCollectionName = DefaultMechanismLists.name(),
                globalMechanismCollectionName = DefaultGlobalMechanismList.name(),
                speedOfLight = 1.0,
                xDim = 10,
                yDim = 10,
                zDim = 3,
                groupEdgeLength = 0.01,
                randomSeed = 100L,
            )
        )

        val universeData1: UniverseData = GenerateUniverseMethodCollection.generate(generateSetting)
        val universeData2: UniverseData = GenerateUniverseMethodCollection.generate(generateSetting)

        assert(universeData1 == universeData2)

        val universe1 = Universe(universeData1)
        val universe2 = Universe(universeData2)

        assert(universe1 != universe2)
    }
}