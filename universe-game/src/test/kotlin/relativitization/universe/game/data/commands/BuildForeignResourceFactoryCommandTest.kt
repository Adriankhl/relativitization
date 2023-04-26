package relativitization.universe.game.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.core.Universe
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.science.application.newResourceFactoryInternalData
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import kotlin.test.Test

internal class BuildForeignResourceFactoryCommandTest {
    @Test
    fun fixedMinimalSelfResourceFactoryTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimalGenerate.name(),
                )
            )
        )
        val view1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        val playerData = view1.get(1)

        val originalFuelRestMassData = playerData.playerInternalData.physicsData().fuelRestMassData

        assert(originalFuelRestMassData.production == 1E100)
        assert(originalFuelRestMassData.trade == 0.0)

        val command = BuildForeignResourceFactoryCommand(
            toId = 1,
            senderTopLeaderId = playerData.topLeaderId(),
            targetCarrierId = 0,
            ownerId = 1,
            resourceFactoryInternalData = playerData.playerInternalData.playerScienceData()
                .playerScienceApplicationData.newResourceFactoryInternalData(
                    outputResourceType = ResourceType.PLANT,
                    qualityLevel = 1.0
                ),
            qualityLevel = 1.0,
            maxNumEmployee = 1.0,
            storedFuelRestMass = 0.0,
            senderFuelLossFractionPerDistance = playerData.playerInternalData.playerScienceData()
                .playerScienceApplicationData.fuelLogisticsLossFractionPerDistance,
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(command)
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        val view2: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        val newPlayerData = view2.get(1)
        val factoryMap = newPlayerData.playerInternalData.popSystemData().carrierDataMap.getValue(
            0
        ).allPopData.labourerPopData.resourceFactoryMap

        assert(factoryMap.size == 1)
    }
}