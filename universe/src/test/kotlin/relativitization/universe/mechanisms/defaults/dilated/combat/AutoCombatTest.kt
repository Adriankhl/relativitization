package relativitization.universe.mechanisms.defaults.dilated.combat

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.commands.DeclareWarCommand
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.generate.method.testing.TestingFixedMinimal
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

        val view6At5 = universe.getUniverse3DViewAtPlayer(5)
        val view6At6 = universe.getUniverse3DViewAtPlayer(6)

        assert(view6At5.getCurrentPlayerData().double4D.x == 1.5)
        assert(view6At6.getCurrentPlayerData().double4D.x == 1.4)
        assert(view6At5.getCurrentPlayerData().groupId != view6At6.getCurrentPlayerData().groupId)

        assert(
            view6At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )
        assert(
            view6At6.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    5 to listOf(
                        DeclareWarCommand(
                            toId = 6,
                            fromId = 5,
                            fromInt4D = view6At5.getCurrentPlayerData().int4D,
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


        val finalViewAt5 = universe.getUniverse3DViewAtPlayer(5)
        val finalViewAt6 = universe.getUniverse3DViewAtPlayer(6)
        assert(finalViewAt5.getCurrentPlayerData().isSubOrdinateOrSelf(6))
        assert(!finalViewAt6.getCurrentPlayerData().isTopLeader())
        assert(finalViewAt6.getCurrentPlayerData().playerInternalData.directLeaderId == 5)
    }
}