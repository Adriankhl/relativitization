package relativitization.universe.data.commands

import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.component.economy.ResourceQualityClass
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.data.component.physics.FuelRestMassData
import relativitization.universe.data.component.physics.Int3D
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.data.component.popsystem.pop.labourer.factory.FactoryInternalData
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.UniverseGenerationCollection
import kotlin.test.Test

internal class BuildForeignFactoryCommandTest {
    @Test
    fun fixedMinimalSelfFuelFactoryTest() {
        val universe = Universe(UniverseGenerationCollection.generate(GenerateSettings()), ".")
        val view7: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        val playerData = view7.get(1)

        val originalFuelRestMassData = playerData.playerInternalData.physicsData().fuelRestMassData

        assert(originalFuelRestMassData.production == 1.0)
        assert(originalFuelRestMassData.trade == 1.0)

        val command = BuildForeignFactoryCommand(
            toId = 1,
            fromId = 1,
            fromInt4D = playerData.int4D,
            senderTopLeaderId = playerData.topLeaderId(),
            targetCarrierId = 0,
            ownerId = 1,
            factoryInternalData = playerData.playerInternalData.playerScienceData().playerScienceProductData.newFactoryInternalData(
                outputResourceType = ResourceType.FUEL,
                qualityLevel = 1.0
            ),
            qualityLevel = 1.0,
            storedFuelRestMass = 0.0
        )
    }
}