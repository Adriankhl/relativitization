package relativitization.universe.game

import kotlinx.coroutines.runBlocking
import relativitization.universe.core.Universe
import relativitization.universe.core.data.MutableUniverseSettings
import relativitization.universe.core.data.UniverseData
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.data.commands.DefaultCommandAvailability
import relativitization.universe.game.generate.random.RandomOneStarPerPlayerGenerate
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import relativitization.universe.game.global.DefaultGlobalMechanismList
import relativitization.universe.game.mechanisms.DefaultMechanismLists
import kotlin.math.pow
import kotlin.test.Test

internal class UniverseTest {
    @Test
    fun saveLoadTest() {
        GameUniverseInitializer.initialize()

        val generateSetting = GenerateSettings(
            generateMethod = TestingFixedMinimalGenerate.name(),
            numPlayer = 4,
            numHumanPlayer = 2,
            otherIntMap = mutableMapOf("numExtraStellarSystem" to 3),
            otherDoubleMap = mutableMapOf("initialPopulation" to 1E6),
            universeSettings = MutableUniverseSettings(universeName = "save-load-test")
        )
        val universeData: UniverseData = GenerateUniverseMethodCollection.generate(generateSetting)
        val universe = Universe(universeData)
        universe.saveAll()
        val universeLoad = Universe.loadUniverseLatest(
            universeName = "save-load-test",
            programDir = ".",
            shouldRandomizeSeed = false
        )

        //println(universeLoad)

        assert(universe.getCurrentPlayerDataList() == universeLoad.getCurrentPlayerDataList())
    }

    @Test
    fun randomSeedBreakdownTest() {
        GameUniverseInitializer.initialize()

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

        val universe1 = Universe(universeData1)
        val universe2 = Universe(universeData2)

        assert(
            universe1.getUniverse3DViewAtPlayer(1) == universe2.getUniverse3DViewAtPlayer(1)
        )

        runBlocking {
            for (i in 1..10) {
                assert(universe1.computeAICommands() == universe2.computeAICommands())
                assert(
                    universe1.getUniverse3DViewAtPlayer(1) ==
                            universe2.getUniverse3DViewAtPlayer(1)
                )
            }
        }

        runBlocking {
            for (i in 1..10) {
                universe1.postProcessUniverse(
                    mapOf(),
                    universe1.computeAICommands()
                )

                universe2.postProcessUniverse(
                    mapOf(),
                    universe2.computeAICommands()
                )

                assert(
                    universe1.getUniverse3DViewAtPlayer(1) ==
                            universe2.getUniverse3DViewAtPlayer(1)
                )
            }
        }

        runBlocking {
            for (i in 1..10) {
                universe1.preProcessUniverse()

                universe2.preProcessUniverse()

                assert(
                    universe1.getUniverse3DViewAtPlayer(1) ==
                            universe2.getUniverse3DViewAtPlayer(1)
                )
            }
        }
    }

    @Test
    fun randomSeedTest() {
        GameUniverseInitializer.initialize()

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

    @Test
    fun timeDilationTest() {
        GameUniverseInitializer.initialize()

        val generateSetting = GenerateSettings(
            generateMethod = TestingFixedMinimalGenerate.name(),
            numPlayer = 4,
            numHumanPlayer = 2,
            otherIntMap = mutableMapOf("numExtraStellarSystem" to 3),
            otherDoubleMap = mutableMapOf("initialPopulation" to 1E6),
            universeSettings = MutableUniverseSettings(universeName = "time-dilation-test")
        )
        val universeData: UniverseData = GenerateUniverseMethodCollection.generate(generateSetting)
        val universe = Universe(universeData)

        val view1 = universe.getUniverse3DViewAtPlayer(8)

        assert(view1.getCurrentPlayerData().timeDilationCounter == 0.0)
        assert(view1.getCurrentPlayerData().isTimeDilationActionTurn)

        universe.pureAIStep()

        val view2 = universe.getUniverse3DViewAtPlayer(8)

        assert((view2.getCurrentPlayerData().timeDilationCounter - 0.6).pow(2) < 0.01)
        assert(!view2.getCurrentPlayerData().isTimeDilationActionTurn)

        universe.pureAIStep()

        val view3 = universe.getUniverse3DViewAtPlayer(8)

        assert((view3.getCurrentPlayerData().timeDilationCounter - 0.2).pow(2) < 0.01)
        assert(view3.getCurrentPlayerData().isTimeDilationActionTurn)

        for (i in (1..22)) {
            universe.pureAIStep()
        }

        val view4 = universe.getUniverse3DViewAtPlayer(8)

        assert((view4.getCurrentPlayerData().timeDilationCounter - 0.4).pow(2) < 0.01)
        assert(view4.getCurrentPlayerData().isTimeDilationActionTurn)

    }
}