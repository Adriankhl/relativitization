package relativitization.universe.mechanisms.defaults.production

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import kotlin.test.Test

internal class FuelFactoryProductionTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(GenerateUniverseMethodCollection.generate(GenerateSettings()), ".")


        runBlocking {
            universe.postProcessUniverse(
                mapOf(),
                mapOf()
            )
            universe.preProcessUniverse()
        }


        val view8: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        assert(view8.get(1).playerInternalData.physicsData().fuelRestMassData.movement == 100.0)
        assert(view8.get(1).playerInternalData.physicsData().fuelRestMassData.production == 30.0)
        assert(view8.get(1).playerInternalData.physicsData().fuelRestMassData.trade == 3.0 + 1E6)
    }
}