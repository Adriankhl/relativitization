package relativitization.universe.game.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.game.Universe
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.game.data.events.AskToMergeCarrierEvent
import relativitization.universe.game.generate.GenerateSettings
import relativitization.universe.game.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import kotlin.test.Test

internal class MergeCarrierCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimalGenerate.name(),
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