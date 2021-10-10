package relativitization.universe.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.UniverseGenerationCollection
import kotlin.test.Test

internal class BuildForeignResourceFactoryCommandTest {
    @Test
    fun fixedMinimalSelfResourceFactoryTest() {
        val universe = Universe(UniverseGenerationCollection.generate(GenerateSettings()), ".")
        val view7: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        val playerData = view7.get(1)

        val originalFuelRestMassData = playerData.playerInternalData.physicsData().fuelRestMassData

        assert(originalFuelRestMassData.production == 30.0)
        assert(originalFuelRestMassData.trade == 1.0)

        val command = BuildForeignResourceFactoryCommand(
            toId = 1,
            fromId = 1,
            fromInt4D = playerData.int4D,
            senderTopLeaderId = playerData.topLeaderId(),
            targetCarrierId = 0,
            ownerId = 1,
            resourceFactoryInternalData = playerData.playerInternalData.playerScienceData().playerScienceProductData.newResourceFactoryInternalData(
                outputResourceType = ResourceType.PLANT,
                qualityLevel = 1.0
            ),
            qualityLevel = 1.0,
            storedFuelRestMass = 0.0
        )

        runBlocking {
            universe.postProcessUniverse(mapOf(
                1 to listOf(command)),
                mapOf(
                    2 to listOf(),
                    3 to listOf(),
                    4 to listOf()
                )
            )
            universe.preProcessUniverse()
        }

        val view8: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        val newPlayerData = view8.get(1)
        val factoryMap = newPlayerData.playerInternalData.popSystemData().carrierDataMap.getValue(0).allPopData.labourerPopData.resourceFactoryMap
        println(factoryMap)
        assert(factoryMap.size == 1)
    }
}