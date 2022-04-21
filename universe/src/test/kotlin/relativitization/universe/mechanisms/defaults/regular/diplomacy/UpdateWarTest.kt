package relativitization.universe.mechanisms.defaults.regular.diplomacy

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.DeclareWarCommand
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.generate.method.testing.TestingFixedMinimal
import kotlin.test.Test

internal class UpdateWarTest {
    @Test
    fun subordinateDefensiveWarTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimal.name(),
                )
            )
        )

        val view1At5: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(5)

        runBlocking {
            universe.postProcessUniverse(
                mapOf(),
                mapOf(
                    5 to listOf(
                        DeclareWarCommand(
                            toId = 2,
                            fromId = 5,
                            fromInt4D = view1At5.getCurrentPlayerData().int4D
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