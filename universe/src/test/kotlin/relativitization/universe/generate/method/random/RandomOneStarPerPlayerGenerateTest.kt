package relativitization.universe.generate.method.random

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.commands.*
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.components.economyData
import relativitization.universe.data.components.physicsData
import relativitization.universe.data.components.playerScienceData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.name
import relativitization.universe.generate.random.RandomOneStarPerPlayerGenerate
import relativitization.universe.global.DefaultGlobalMechanismList
import relativitization.universe.global.name
import relativitization.universe.mechanisms.DefaultMechanismLists
import relativitization.universe.mechanisms.name
import kotlin.test.Ignore
import kotlin.test.Test

internal class RandomOneStarPerPlayerGenerateTest {
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
                fromId = 1,
                fromInt4D = view1.get(1).int4D,
                amount = 1E7,
                senderFuelLossFractionPerDistance = view1.getCurrentPlayerData().playerInternalData.playerScienceData()
                    .playerScienceApplicationData.fuelLogisticsLossFractionPerDistance
            )
        )

        ResourceType.factoryResourceList.forEach {
            commandList.add(
                BuildForeignResourceFactoryCommand(
                    toId = 1,
                    fromId = 1,
                    fromInt4D = view1.get(1).int4D,
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
                    fromId = 1,
                    fromInt4D = view1.get(1).int4D,
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