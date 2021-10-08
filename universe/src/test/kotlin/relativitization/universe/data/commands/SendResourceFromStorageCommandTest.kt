package relativitization.universe.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.component.economy.ResourceQualityClass
import relativitization.universe.data.component.economy.ResourceQualityData
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.data.component.physics.Int3D
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.UniverseGenerationCollection
import kotlin.test.Test

internal class SendResourceFromStorageCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(UniverseGenerationCollection.generate(GenerateSettings()), ".")
        val view7: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        val playerData1 = view7.get(1)
        val playerData2 = view7.get(2)

        val resourceData1 = playerData1.playerInternalData.economyData().resourceData
        val resourceData2 = playerData2.playerInternalData.economyData().resourceData

        assert(resourceData1.getTradeResourceAmount(ResourceType.PLANT, ResourceQualityClass.FIRST) == 5.0)
        assert(resourceData2.getTradeResourceAmount(ResourceType.PLANT, ResourceQualityClass.FIRST) == 0.0)

        val command = SendResourceFromStorageCommand(
            toId = 2,
            fromId = 1,
            fromInt4D = playerData1.int4D,
            resourceType = ResourceType.PLANT,
            resourceQualityClass = ResourceQualityClass.FIRST,
            resourceQualityData = resourceData1.getResourceQuality(ResourceType.PLANT, ResourceQualityClass.FIRST),
            amount = 3.0,
            senderResourceLossFractionPerDistance = playerData1.playerInternalData.playerScienceData().playerScienceProductData.resourceLogisticsLossFractionPerDistance
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

        val newPlayerData1 = view8.get(1)
        val newPlayerData2 = view8.get(2)

        val newResourceData1 = newPlayerData1.playerInternalData.economyData().resourceData
        val newResourceData2 = newPlayerData2.playerInternalData.economyData().resourceData

        assert(newResourceData1.getTradeResourceAmount(ResourceType.PLANT, ResourceQualityClass.FIRST) == 2.0)
        assert(newResourceData2.getTradeResourceAmount(ResourceType.PLANT, ResourceQualityClass.FIRST) == 3.0)

    }
}