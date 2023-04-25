package relativitization.universe.game.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.game.Universe
import relativitization.universe.game.generate.GenerateSettings
import relativitization.universe.game.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import kotlin.test.Test

internal class SplitCarrierCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimalGenerate.name(),
                )
            )
        )

        assert(!universe.availablePlayers().contains(9))

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(
                        SplitCarrierCommand(
                            toId = 1,
                            carrierIdList = listOf(1),
                            storageFraction = 0.1,
                        )
                    )
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        assert(universe.availablePlayers().contains(9))
    }
}