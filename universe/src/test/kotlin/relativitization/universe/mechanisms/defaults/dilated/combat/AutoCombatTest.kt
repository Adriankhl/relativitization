package relativitization.universe.mechanisms.defaults.dilated.combat

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.commands.DeclareWarCommand
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.name
import relativitization.universe.generate.testing.TestingFixedMinimal
import kotlin.test.Test

internal class AutoCombatTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimal.name(),
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
                            fromId = 5,
                            fromInt4D = view1At5.getCurrentPlayerData().int4D,
                        )
                    )
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        runBlocking {
            for (i in 1..10) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }


        val view2At5 = universe.getUniverse3DViewAtPlayer(5)
        val view2At6 = universe.getUniverse3DViewAtPlayer(6)
        assert(view2At5.getCurrentPlayerData().isSubOrdinateOrSelf(6))
        assert(!view2At6.getCurrentPlayerData().isTopLeader())
        assert(view2At6.getCurrentPlayerData().playerInternalData.directLeaderId == 5)
    }
}