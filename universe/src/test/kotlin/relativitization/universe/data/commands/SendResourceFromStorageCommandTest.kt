package relativitization.universe.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.economyData
import relativitization.universe.data.components.playerScienceData
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.generate.method.testing.TestingFixedMinimal
import kotlin.test.Test

internal class SendResourceFromStorageCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimal.name(),
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
            fromId = 1,
            fromInt4D = playerData1.int4D,
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