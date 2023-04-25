package relativitization.universe.game.mechanisms.defaults.regular.diplomacy

import kotlinx.coroutines.runBlocking
import relativitization.universe.game.Universe
import relativitization.universe.game.data.UniverseData3DAtPlayer
import relativitization.universe.game.data.commands.DeclareWarCommand
import relativitization.universe.game.data.components.diplomacyData
import relativitization.universe.game.generate.GenerateSettings
import relativitization.universe.game.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import kotlin.test.Test

internal class UpdateWarTest {
    @Test
    fun subordinateDefensiveWarTest() {
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