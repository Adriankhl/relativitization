package relativitization.universe.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.components.popSystemData
import relativitization.universe.data.events.AskToMergeCarrierEvent
import relativitization.universe.data.events.name
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.generate.method.testing.TestingFixedMinimal
import kotlin.test.Test

internal class MergeCarrierCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimal.name(),
                )
            )
        )

        val view6At1 = universe.getUniverse3DViewAtPlayer(1)
        val view6At2 = universe.getUniverse3DViewAtPlayer(2)

        assert(view6At1.getCurrentPlayerData().int4D == view6At2.getCurrentPlayerData().int4D)
        assert(view6At1.getCurrentPlayerData().groupId == view6At2.getCurrentPlayerData().groupId)

        val event = AskToMergeCarrierEvent(
            toId = 2,
            fromId = 1
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(
                        AddEventCommand(
                            event = event,
                            fromInt4D = view6At1.getCurrentPlayerData().int4D,
                        )
                    )
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }


        val view7At2 = universe.getUniverse3DViewAtPlayer(2)

        assert(view7At2.getCurrentPlayerData().playerInternalData.eventDataMap.size == 1)

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    2 to listOf(
                        SelectEventChoiceCommand(
                            toId = 2,
                            fromId = 2,
                            fromInt4D = view7At2.getCurrentPlayerData().int4D,
                            eventKey = 0,
                            eventName = event.name(),
                            choice = 0
                        )
                    )
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        val view8At1 = universe.getUniverse3DViewAtPlayer(1)

        assert(!universe.availablePlayers().contains(2))
        assert(view8At1.getCurrentPlayerData().playerInternalData.popSystemData().carrierDataMap.size == 3)
    }
}