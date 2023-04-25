package relativitization.universe.game.mechanisms.defaults.dilated.combat

import kotlinx.coroutines.runBlocking
import relativitization.universe.core.Universe
import relativitization.universe.core.data.MutableUniverseSettings
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

internal class AutoCombatTest {
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