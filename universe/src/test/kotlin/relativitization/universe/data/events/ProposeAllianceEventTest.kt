package relativitization.universe.data.events

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.commands.SelectEventChoiceCommand
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.generate.method.testing.TestingFixedMinimal
import kotlin.test.Test

internal class ProposeAllianceEventTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimal.name(),
                )
            )
        )

        val view6At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        val view6At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)

        assert(
            view6At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData.allyMap
                .isEmpty()
        )

        val event = ProposeAllianceEvent(
            toId = 1,
            fromId = 3
        )

        val command = AddEventCommand(
            event,
            view6At3.getCurrentPlayerData().int4D,
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(),
                mapOf(
                    3 to listOf(command)
                )
            )
            universe.preProcessUniverse()
        }

        runBlocking {
            for (i in 1..5) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }

        val view12At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        assert(
            view12At1.getCurrentPlayerData().playerInternalData.eventDataMap.isNotEmpty()
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(
                        SelectEventChoiceCommand(
                            toId = 1,
                            fromId = 1,
                            fromInt4D = view12At1.getCurrentPlayerData().int4D,
                            eventKey = 0,
                            eventName = ProposeAllianceEvent::class.name(),
                            choice = 0
                        )
                    )
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        val view13At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        val view13At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)

        assert(
            view13At1.getCurrentPlayerData().playerInternalData.eventDataMap.isEmpty()
        )

        assert(
            view13At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isAlly(3)
        )

        assert(
            view13At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .allyMap.isEmpty()
        )


        runBlocking {
            for (i in 1..3) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }

        val viewFinalAt1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        val viewFinalAt3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)

        assert(
            viewFinalAt1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isAlly(3)
        )

        assert(
            viewFinalAt3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isAlly(1)
        )
    }
}