package relativitization.universe.game.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.game.Universe
import relativitization.universe.game.data.components.diplomacyData
import relativitization.universe.game.generate.GenerateSettings
import relativitization.universe.game.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import kotlin.test.Test

internal class DeclareWarCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimalGenerate.name(),
                )
            )
        )

        val view1At5 = universe.getUniverse3DViewAtPlayer(5)
        val view1At6 = universe.getUniverse3DViewAtPlayer(6)

        assert(view1At5.getCurrentPlayerData().double4D.x == 1.5)
        assert(view1At6.getCurrentPlayerData().double4D.x == 1.4)
        assert(view1At5.getCurrentPlayerData().groupId != view1At6.getCurrentPlayerData().groupId)

        assert(
            view1At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )
        assert(
            view1At6.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    5 to listOf(
                        DeclareWarCommand(
                            toId = 6,
                        )
                    )
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        val view2At5 = universe.getUniverse3DViewAtPlayer(5)
        val view2At6 = universe.getUniverse3DViewAtPlayer(6)

        assert(
            view2At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.containsKey(6)
        )

        assert(view2At6.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
            .selfWarDataMap.isEmpty()
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(),
                mapOf()
            )
            universe.preProcessUniverse()
        }


        val view3At5 = universe.getUniverse3DViewAtPlayer(5)
        val view3At6 = universe.getUniverse3DViewAtPlayer(6)


        assert(
            view3At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.containsKey(6)
        )
        assert(
            view3At6.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.containsKey(5)
        )
    }
}