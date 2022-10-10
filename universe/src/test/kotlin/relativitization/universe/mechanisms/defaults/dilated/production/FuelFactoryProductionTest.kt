package relativitization.universe.mechanisms.defaults.dilated.production

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.components.physicsData
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.testing.TestingFixedMinimal
import kotlin.test.Test

internal class FuelFactoryProductionTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimal.name(),
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