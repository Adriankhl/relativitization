package relativitization.universe.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.generate.method.testing.TestingFixedMinimal
import kotlin.test.Test

internal class SplitCarrierCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(GenerateUniverseMethodCollection.generate(GenerateSettings(
            generateMethod = TestingFixedMinimal.name(),
        )), ".")

        val view6 = universe.getUniverse3DViewAtPlayer(1)

        assert(!universe.availablePlayers().contains(7))

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(
                        SplitCarrierCommand(
                            toId = 1,
                            fromId = 1,
                            fromInt4D = view6.getCurrentPlayerData().int4D,
                            carrierIdList = listOf(1),
                            storageFraction = 0.1,
                        )
                    )
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        assert(universe.availablePlayers().contains(7))

    }
}