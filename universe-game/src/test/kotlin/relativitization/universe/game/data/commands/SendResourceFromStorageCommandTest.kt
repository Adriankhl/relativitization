package relativitization.universe.game.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.core.Universe
import relativitization.universe.core.data.MutableUniverseSettings
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.GameUniverseInitializer
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.economy.getResourceQuality
import relativitization.universe.game.data.components.defaults.economy.getTradeResourceAmount
import relativitization.universe.game.data.components.economyData
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import relativitization.universe.game.global.DefaultGlobalMechanismList
import relativitization.universe.game.mechanisms.DefaultMechanismLists
import kotlin.test.Test

internal class SendResourceFromStorageCommandTest {
    @Test
    fun fixedMinimalTest() {
        GameUniverseInitializer.initialize()

        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimalGenerate.name(),
                    universeSettings = MutableUniverseSettings(
                        commandCollectionName = DefaultCommandAvailability.name(),
                        mechanismCollectionName = DefaultMechanismLists.name(),
                        globalMechanismCollectionName = DefaultGlobalMechanismList.name(),
                    ),
                )
            )
        )
        val view1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        val playerData1 = view1.get(1)
        val playerData2 = view1.get(2)

        val resourceData1 = playerData1.playerInternalData.economyData().resourceData
        val resourceData2 = playerData2.playerInternalData.economyData().resourceData

        assert(
            resourceData1.getTradeResourceAmount(
                ResourceType.PLANT,
                ResourceQualityClass.THIRD
            ) == 1.25
        )
        assert(
            resourceData2.getTradeResourceAmount(
                ResourceType.PLANT,
                ResourceQualityClass.THIRD
            ) == 0.0
        )

        val command = SendResourceFromStorageCommand(
            toId = 2,
            resourceType = ResourceType.PLANT,
            resourceQualityClass = ResourceQualityClass.THIRD,
            resourceQualityData = resourceData1.getResourceQuality(
                ResourceType.PLANT,
                ResourceQualityClass.THIRD
            ),
            amount = 1.0,
            senderResourceLossFractionPerDistance = playerData1.playerInternalData
                .playerScienceData().playerScienceApplicationData
                .resourceLogisticsLossFractionPerDistance
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

        val newPlayerData1 = view2.get(1)
        val newPlayerData2 = view2.get(2)

        val newResourceData1 = newPlayerData1.playerInternalData.economyData().resourceData
        val newResourceData2 = newPlayerData2.playerInternalData.economyData().resourceData

        // Times 0.99 to account for resource decay
        assert(
            newResourceData1.getTradeResourceAmount(
                ResourceType.PLANT,
                ResourceQualityClass.THIRD
            ) == 1.25 * 0.99
        )
        assert(
            newResourceData2.getTradeResourceAmount(
                ResourceType.PLANT,
                ResourceQualityClass.THIRD
            ) == 0.25 * 0.99
        )

    }
}