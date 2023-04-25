package relativitization.universe.game.mechanisms.defaults.dilated.production

import kotlinx.coroutines.runBlocking
import relativitization.universe.game.Universe
import relativitization.universe.game.data.UniverseData3DAtPlayer
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.generate.GenerateSettings
import relativitization.universe.game.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import kotlin.test.Test

internal class FuelFactoryProductionTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimalGenerate.name(),
                )
            )
        )


        runBlocking {
            universe.postProcessUniverse(
                mapOf(),
                mapOf()
            )
            universe.preProcessUniverse()
        }


        val view1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        assert(view1.get(1).playerInternalData.physicsData().fuelRestMassData.movement == 0.0)
        assert(view1.get(1).playerInternalData.physicsData().fuelRestMassData.storage == 2.0 + 1E6)
    }
}