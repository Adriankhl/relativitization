package relativitization.universe.data.commands

import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.component.economy.ResourceQualityClass
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.UniverseGenerationCollection
import kotlin.test.Test

internal class BuildForeignFactoryCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(UniverseGenerationCollection.generate(GenerateSettings()), ".")
        val view7: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        view7.get(2).playerInternalData.economyData().resourceData.getTradeResourceAmount(
            ResourceType.PLANT,
            ResourceQualityClass.FIRST,
        )
    }
}