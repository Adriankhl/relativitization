package relativitization.universe.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.name
import relativitization.universe.generate.testing.TestingFixedMinimal
import kotlin.test.Test

internal class SplitCarrierCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimal.name(),
                )
            )
        )

        val view1 = universe.getUniverse3DViewAtPlayer(1)

        assert(!universe.availablePlayers().contains(7))

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(
                        SplitCarrierCommand(
                            toId = 1,
                            fromId = 1,
                            fromInt4D = view1.getCurrentPlayerData().int4D,
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