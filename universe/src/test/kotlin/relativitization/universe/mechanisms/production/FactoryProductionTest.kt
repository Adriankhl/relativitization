package relativitization.universe.mechanisms.production

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.UniverseGenerationCollection
import kotlin.test.Test

internal class FactoryProductionTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(UniverseGenerationCollection.generate(GenerateSettings()), ".")


        runBlocking {
            universe.postProcessUniverse(mapOf(
                1 to listOf()),
                mapOf(
                    2 to listOf(),
                    3 to listOf(),
                    4 to listOf()
                )
            )
            universe.preProcessUniverse()
        }


        val view8: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        assert(view8.get(1).playerInternalData.physicsData().fuelRestMassData.movement == 102.0)
    }
}