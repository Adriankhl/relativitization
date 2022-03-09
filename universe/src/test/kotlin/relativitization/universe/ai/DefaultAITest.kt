package relativitization.universe.ai

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.commands.DefaultCommandAvailability
import relativitization.universe.data.commands.name
import relativitization.universe.data.components.economyData
import relativitization.universe.data.components.physicsData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.generate.method.random.RandomOneStarPerPlayerGenerate
import relativitization.universe.global.DefaultGlobalMechanismList
import relativitization.universe.global.name
import relativitization.universe.mechanisms.DefaultMechanismLists
import relativitization.universe.mechanisms.name
import kotlin.test.Test

internal class DefaultAITest {
    @Test
    fun longRunTest() {
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
                xDim = 10,
                yDim = 10,
                zDim = 3,
                groupEdgeLength = 0.01,
            )
        )

        val universe = Universe(GenerateUniverseMethodCollection.generate(generateSetting))

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
            for (turn in 1..100) {
                val aiCommandMap = universe.computeAICommands()

                universe.postProcessUniverse(
                    mapOf(),
                    aiCommandMap
                )
                universe.preProcessUniverse()
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
        popSystemData.carrierDataMap
        carrierData[0]

        val totalAdultPopulation: Double = finalView.playerDataMap.values.sumOf {
            it.playerInternalData.popSystemData().totalAdultPopulation()
        }

        // Ensure population has grown
        assert(totalAdultPopulation > initialAdultPopulation)
        if (finalView.playerDataMap.size >= 2) {
            val nonEmptyCube: List<Map<Int, List<Int>>> = finalView.playerId3DMap.flatten().flatten().filter {
                it.isNotEmpty()
            }
            assert(nonEmptyCube.size >= 2)
        }
    }
}