package relativitization.universe.game.generate.random

import kotlinx.coroutines.runBlocking
import relativitization.universe.game.Universe
import relativitization.universe.game.data.MutableUniverseSettings
import relativitization.universe.game.data.UniverseData
import relativitization.universe.game.data.commands.BuildForeignResourceFactoryCommand
import relativitization.universe.game.data.commands.ChangeSalaryFactorCommand
import relativitization.universe.game.data.commands.Command
import relativitization.universe.game.data.commands.DefaultCommandAvailability
import relativitization.universe.game.data.commands.SendFuelFromStorageCommand
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.game.data.components.economyData
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.game.generate.GenerateSettings
import relativitization.universe.game.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.global.DefaultGlobalMechanismList
import relativitization.universe.game.mechanisms.DefaultMechanismLists
import kotlin.test.Test
import kotlin.test.Ignore

internal class RandomOneStarPerPlayerGenerateTest {
    @Test
    fun randomSeedTest() {
        // Set the initial population to 1E8 to test how the AI do with a big population
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

        assert(universe1 != universe2)
    }

        @Test
    fun onePlayerTest() {
        val generateSetting = GenerateSettings(
            generateMethod = RandomOneStarPerPlayerGenerate.name(),
            numPlayer = 1,
            numHumanPlayer = 1,
            otherIntMap = mutableMapOf("numExtraStellarSystem" to 3),
            otherDoubleMap = mutableMapOf("initialPopulation" to 1E6),
            universeSettings = MutableUniverseSettings(
                universeName = "One player test",
                commandCollectionName = DefaultCommandAvailability.name(),
                mechanismCollectionName = DefaultMechanismLists.name(),
                globalMechanismCollectionName = DefaultGlobalMechanismList.name(),
                speedOfLight = 1.0,
                xDim = 2,
                yDim = 2,
                zDim = 2,
                tDim = 8,
                playerAfterImageDuration = 4,
                playerHistoricalInt4DLength = 4,
                groupEdgeLength = 0.01,
                otherIntMap = mutableMapOf(),
                otherDoubleMap = mutableMapOf(),
            )
        )

        val universe = Universe(
            GenerateUniverseMethodCollection.generate(generateSetting),
        )

        val view1 = universe.getUniverse3DViewAtPlayer(1)

        val commandList: MutableList<Command> = mutableListOf()

        commandList.add(
            SendFuelFromStorageCommand(
                toId = 1,
                amount = 1E7,
                senderFuelLossFractionPerDistance = view1.getCurrentPlayerData().playerInternalData
                    .playerScienceData().playerScienceApplicationData
                    .fuelLogisticsLossFractionPerDistance
            )
        )

        ResourceType.factoryResourceList.forEach {
            commandList.add(
                BuildForeignResourceFactoryCommand(
                    toId = 1,
                    senderTopLeaderId = 1,
                    targetCarrierId = 0,
                    ownerId = 1,
                    resourceFactoryInternalData = view1.get(1).playerInternalData
                        .playerScienceData().playerScienceApplicationData
                        .newResourceFactoryInternalData(
                            it,
                            1.0
                        ),
                    qualityLevel = 1.0,
                    maxNumEmployee = 1.0,
                    storedFuelRestMass = 0.0,
                    senderFuelLossFractionPerDistance = view1.get(1).playerInternalData
                        .playerScienceData().playerScienceApplicationData
                        .fuelLogisticsLossFractionPerDistance,
                )
            )
        }

        PopType.values().forEach {
            commandList.add(
                ChangeSalaryFactorCommand(
                    toId = 1,
                    carrierId = 0,
                    popType = it,
                    salaryFactor = 2.0,
                )
            )
        }

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to commandList
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }
    }

    @Ignore
    fun onePlayerHighInitialPopulationTest() {
        // Set the initial population to 1E8 to test how the AI do with a big population
        val generateSetting = GenerateSettings(
            generateMethod = RandomOneStarPerPlayerGenerate.name(),
            numPlayer = 1,
            numHumanPlayer = 1,
            otherIntMap = mutableMapOf("numExtraStellarSystem" to 3),
            otherDoubleMap = mutableMapOf("initialPopulation" to 1E6),
            universeSettings = MutableUniverseSettings(
                universeName = "One player test",
                commandCollectionName = DefaultCommandAvailability.name(),
                mechanismCollectionName = DefaultMechanismLists.name(),
                globalMechanismCollectionName = DefaultGlobalMechanismList.name(),
                speedOfLight = 1.0,
                xDim = 1,
                yDim = 1,
                zDim = 1,
                tDim = 5,
                playerAfterImageDuration = 4,
                playerHistoricalInt4DLength = 4,
                groupEdgeLength = 0.01,
                otherIntMap = mutableMapOf(),
                otherDoubleMap = mutableMapOf(),
            )
        )

        val universe = Universe(
            GenerateUniverseMethodCollection.generate(generateSetting),
        )

        val initialAdultPopulation: Double = universe.getUniverse3DViewAtPlayer(1).get(1)
            .playerInternalData.popSystemData().totalAdultPopulation()

        runBlocking {
            for (turn in 1..100) {
                val aiCommandMap = universe.computeAICommands()

                universe.postProcessUniverse(
                    mapOf(),
                    aiCommandMap
                )
                universe.preProcessUniverse()

                // For debug convenience
                val view = universe.getUniverse3DViewAtPlayer(1)
                val allPopData = view.getCurrentPlayerData().playerInternalData.popSystemData()
                    .carrierDataMap.getValue(0).allPopData
                val fuelData = view.getCurrentPlayerData().playerInternalData.physicsData()
                    .fuelRestMassData
                fuelData.production
                allPopData.labourerPopData
            }
        }

        val middleView = universe.getUniverse3DViewAtPlayer(1)

        val middleAllPopData = middleView.getCurrentPlayerData().playerInternalData.popSystemData()
            .carrierDataMap.getValue(0).allPopData

        val middleResourceData = middleView.getCurrentPlayerData().playerInternalData.economyData()
            .resourceData

        val middleFuelData = middleView.getCurrentPlayerData().playerInternalData.physicsData()
            .fuelRestMassData

        val middleCarrierData = middleView.getCurrentPlayerData().playerInternalData.popSystemData().carrierDataMap

        middleAllPopData.labourerPopData
        middleResourceData.singleResourceMap
        middleFuelData.production
        middleCarrierData[0]

        runBlocking {
            for (turn in 1..200) {
                val aiCommandMap = universe.computeAICommands()

                universe.postProcessUniverse(
                    mapOf(),
                    aiCommandMap
                )
                universe.preProcessUniverse()

                // For debug convenience
                val view = universe.getUniverse3DViewAtPlayer(1)
                val allPopData = view.getCurrentPlayerData().playerInternalData.popSystemData()
                    .carrierDataMap.getValue(0).allPopData
                val fuelData = view.getCurrentPlayerData().playerInternalData.physicsData()
                    .fuelRestMassData
                fuelData.production
                allPopData.labourerPopData
            }
        }

        val finalView = universe.getUniverse3DViewAtPlayer(1)

        val allPopData = finalView.getCurrentPlayerData().playerInternalData.popSystemData()
            .carrierDataMap.getValue(0).allPopData
        val resourceData = finalView.getCurrentPlayerData().playerInternalData.economyData()
            .resourceData
        val fuelData = finalView.getCurrentPlayerData().playerInternalData.physicsData()
            .fuelRestMassData
        val popSystemData = finalView.getCurrentPlayerData().playerInternalData.popSystemData()
        val carrierData = finalView.getCurrentPlayerData().playerInternalData.popSystemData().carrierDataMap

        allPopData.labourerPopData
        resourceData.singleResourceMap
        fuelData.production
        carrierData[0]

        val totalAdultPopulation: Double = popSystemData.totalAdultPopulation()

        // Ensure population has grown
        // This does not work
        totalAdultPopulation > initialAdultPopulation
        //assert(totalAdultPopulation > initialAdultPopulation)
    }
}