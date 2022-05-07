package relativitization.universe.data.events

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.commands.DeclareWarCommand
import relativitization.universe.data.commands.SelectEventChoiceCommand
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.name
import relativitization.universe.generate.testing.TestingFixedMinimal
import kotlin.test.Test

internal class CallAllyToWarEventTest {
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

        val event = ProposeAllianceEvent(
            toId = 3,
            fromId = 1
        )

        val proposeAllianceCommand = AddEventCommand(
            event,
            view1At1.getCurrentPlayerData().int4D,
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(proposeAllianceCommand)
                ),
                mapOf(),
            )
            universe.preProcessUniverse()
        }

        runBlocking {
            for (i in 1..3) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }

        val view2At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)

        runBlocking {
            universe.postProcessUniverse(
                mapOf(),
                mapOf(
                    3 to listOf(
                        SelectEventChoiceCommand(
                            toId = 3,
                            fromId = 3,
                            fromInt4D = view2At3.getCurrentPlayerData().int4D,
                            eventKey = 0,
                            eventName = ProposeAllianceEvent::class.name(),
                            choice = 0
                        )
                    )
                ),
            )
            universe.preProcessUniverse()
        }

        runBlocking {
            for (i in 1..3) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }

        val view3At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        val view3At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)

        assert(
            view3At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isAlly(3)
        )

        assert(
            view3At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isAlly(1)
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(
                        DeclareWarCommand(
                            toId = 5,
                            fromId = 1,
                            fromInt4D = view3At1.getCurrentPlayerData().int4D,
                        ),
                        AddEventCommand(
                            CallAllyToWarEvent(
                                toId = 3,
                                fromId = 1,
                                warTargetId = 5
                            ),
                            fromInt4D = view3At1.getCurrentPlayerData().int4D,
                        )
                    )
                ),
                mapOf(),
            )
            universe.preProcessUniverse()
        }

        val view4At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        val view4At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)

        assert(
            view4At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isEnemy(5)
        )

        assert(
            !view4At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isEnemy(5)
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

        val view5At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)

        runBlocking {
            universe.postProcessUniverse(
                mapOf(),
                mapOf(
                    3 to listOf(
                        SelectEventChoiceCommand(
                            toId = 3,
                            fromId = 3,
                            fromInt4D = view5At3.getCurrentPlayerData().int4D,
                            eventKey = 0,
                            eventName = CallAllyToWarEvent::class.name(),
                            choice = 0
                        )
                    )
                ),
            )
            universe.preProcessUniverse()
        }

        val view6At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        val view6At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)

        assert(
            view6At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isEnemy(5)
        )

        assert(
            view6At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .allyWarDataMap.containsKey(1)
        )

        assert(
            view6At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isEnemy(5)
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

        val view7At5: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(5)

        assert(
            view7At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isEnemy(1)
        )

        assert(
            view7At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isEnemy(3)
        )

        assert(
            view7At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isEnemy(2)
        )

        assert(
            view7At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.keys == setOf(1)
        )
    }
}