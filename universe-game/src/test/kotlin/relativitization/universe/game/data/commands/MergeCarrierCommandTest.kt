package relativitization.universe.game.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.core.Universe
import relativitization.universe.core.data.MutableUniverseSettings
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.GameUniverseInitializer
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.game.data.events.AskToMergeCarrierEvent
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import relativitization.universe.game.global.DefaultGlobalMechanismList
import relativitization.universe.game.mechanisms.DefaultMechanismLists
import kotlin.test.Test

internal class MergeCarrierCommandTest {
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

        val view1At1 = universe.getUniverse3DViewAtPlayer(1)
        val view1At2 = universe.getUniverse3DViewAtPlayer(2)

        assert(view1At1.getCurrentPlayerData().int4D == view1At2.getCurrentPlayerData().int4D)
        assert(view1At1.getCurrentPlayerData().groupId == view1At2.getCurrentPlayerData().groupId)

        val event = AskToMergeCarrierEvent(
            toId = 2,
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(
                        AddEventCommand(
                            event = event,
                        )
                    )
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }


        val view2At2 = universe.getUniverse3DViewAtPlayer(2)

        assert(view2At2.getCurrentPlayerData().playerInternalData.eventDataMap.size == 1)

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    2 to listOf(
                        SelectEventChoiceCommand(
                            toId = 2,
                            eventKey = 0,
                            choice = 0
                        )
                    )
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        val view3At1 = universe.getUniverse3DViewAtPlayer(1)

        assert(!universe.availablePlayers().contains(2))
        assert(view3At1.getCurrentPlayerData().playerInternalData.popSystemData().carrierDataMap.size == 3)
    }
}