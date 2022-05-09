package relativitization.universe

import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.UniverseData
import relativitization.universe.data.commands.DefaultCommandAvailability
import relativitization.universe.data.commands.name
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.name
import relativitization.universe.generate.random.RandomOneStarPerPlayerGenerate
import relativitization.universe.generate.testing.TestingFixedMinimal
import relativitization.universe.global.DefaultGlobalMechanismList
import relativitization.universe.global.name
import relativitization.universe.mechanisms.DefaultMechanismLists
import relativitization.universe.mechanisms.name
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

    @Test
    fun randomSeedTest() {
        val generateSetting = GenerateSettings(
            generateMethod = RandomOneStarPerPlayerGenerate.name(),
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

        for (turn in 1..20) {
            universe1.pureAIStep()
        }

        for (turn in 1..20) {
            universe2.pureAIStep()
        }

        assert(universe1.getUniverse3DViewAtPlayer(1) == universe2.getUniverse3DViewAtPlayer(1))
    }
}