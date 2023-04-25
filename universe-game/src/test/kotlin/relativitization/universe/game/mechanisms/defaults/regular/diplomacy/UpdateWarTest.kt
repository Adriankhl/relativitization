package relativitization.universe.game.mechanisms.defaults.regular.diplomacy

import kotlinx.coroutines.runBlocking
import relativitization.universe.core.Universe
import relativitization.universe.core.data.MutableUniverseSettings
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.game.data.commands.DeclareWarCommand
import relativitization.universe.game.data.components.diplomacyData
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.GameUniverseInitializer
import relativitization.universe.game.data.commands.DefaultCommandAvailability
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import relativitization.universe.game.global.DefaultGlobalMechanismList
import relativitization.universe.game.mechanisms.DefaultMechanismLists
import kotlin.test.Test

internal class UpdateWarTest {
    @Test
    fun subordinateDefensiveWarTest() {
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

        runBlocking {
            universe.postProcessUniverse(
                mapOf(),
                mapOf(
                    5 to listOf(
                        DeclareWarCommand(
                            toId = 2,
                        )
                    )
                ),
            )
            universe.preProcessUniverse()
        }


        runBlocking {
            for (i in (1..3)) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }

        val view2At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        assert(
            view2At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isEnemy(5)
        )

        assert(
            view2At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .hasSubordinateWar(2, 5)
        )
    }
}