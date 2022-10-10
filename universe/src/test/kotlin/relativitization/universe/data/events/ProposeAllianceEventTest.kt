package relativitization.universe.data.events

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.commands.SelectEventChoiceCommand
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.testing.TestingFixedMinimal
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

        val view1At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        val view1At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)

        assert(
            view1At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .allyMap.size == 1
        )

        val event = ProposeAllianceEvent(
            toId = 1,
            fromId = 3
        )

        val command = AddEventCommand(
            event,
            view1At3.getCurrentPlayerData().int4D,
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

        val view2At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        assert(
            view2At1.getCurrentPlayerData().playerInternalData.eventDataMap.isNotEmpty()
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(
                        SelectEventChoiceCommand(
                            toId = 1,
                            fromId = 1,
                            fromInt4D = view2At1.getCurrentPlayerData().int4D,
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

        val view3At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        val view3At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)

        assert(
            view3At1.getCurrentPlayerData().playerInternalData.eventDataMap.isEmpty()
        )

        assert(
            view3At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isAlly(3)
        )

        assert(
            view3At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
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

        val view4At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        val view4At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)

        assert(
            view4At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isAlly(3)
        )

        assert(
            view4At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isAlly(1)
        )

        runBlocking {
            for (i in 1..10) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }


        val view5At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        val view5At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)

        assert(
            view5At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isAlly(3)
        )

        assert(
            view5At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isAlly(1)
        )
    }
}