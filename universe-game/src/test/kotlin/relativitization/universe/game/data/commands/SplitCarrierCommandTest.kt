package relativitization.universe.game.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.core.Universe
import relativitization.universe.core.data.MutableUniverseSettings
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.GameUniverseInitializer
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import relativitization.universe.game.global.DefaultGlobalMechanismList
import relativitization.universe.game.mechanisms.DefaultMechanismLists
import kotlin.test.Test

internal class SplitCarrierCommandTest {
    @Test
    fun fixedMinimalTest() {
        GameUniverseInitializer.initialize()

        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimalGenerate.name(),
                    universeSettings = MutableUniverseSettings(
                        commandCollectionName = DefaultCommandAvailability.name(),
                        mechanismCollectionName = DefaultMechanismLists.name(),
                        globalMechanismCollectionName = DefaultGlobalMechanismList.name(),
                    ),
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