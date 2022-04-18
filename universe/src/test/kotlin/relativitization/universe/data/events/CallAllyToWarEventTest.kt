package relativitization.universe.data.events

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.commands.DeclareWarCommand
import relativitization.universe.data.commands.SelectEventChoiceCommand
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.generate.method.testing.TestingFixedMinimal
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

        val view6At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        val event = ProposeAllianceEvent(
            toId = 3,
            fromId = 1
        )

        val proposeAllianceCommand = AddEventCommand(
            event,
            view6At1.getCurrentPlayerData().int4D,
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

        val view10At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)

        runBlocking {
            universe.postProcessUniverse(
                mapOf(),
                mapOf(
                    3 to listOf(
                        SelectEventChoiceCommand(
                            toId = 3,
                            fromId = 3,
                            fromInt4D = view10At3.getCurrentPlayerData().int4D,
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

        val view14At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        val view14At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)

        assert(
            view14At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isAlly(3)
        )

        assert(
            view14At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isAlly(1)
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(
                        DeclareWarCommand(
                            toId = 5,
                            fromId = 1,
                            fromInt4D = view14At1.getCurrentPlayerData().int4D,
                        ),
                        AddEventCommand(
                            CallAllyToWarEvent(
                                toId = 3,
                                fromId = 1,
                                warTargetId = 5
                            ),
                            fromInt4D = view14At1.getCurrentPlayerData().int4D,
                        )
                    )
                ),
                mapOf(),
            )
            universe.preProcessUniverse()
        }

        val view15At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        val view15At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)

        assert(
            view15At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isEnemy(5)
        )

        assert(
            !view15At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
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

        val view18At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)

        runBlocking {
            universe.postProcessUniverse(
                mapOf(),
                mapOf(
                    3 to listOf(
                        SelectEventChoiceCommand(
                            toId = 3,
                            fromId = 3,
                            fromInt4D = view18At3.getCurrentPlayerData().int4D,
                            eventKey = 0,
                            eventName = CallAllyToWarEvent::class.name(),
                            choice = 0
                        )
                    )
                ),
            )
            universe.preProcessUniverse()
        }

        val view19At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        val view19At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)

        assert(
            view19At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isEnemy(5)
        )

        assert(
            view19At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .allyWarDataMap.containsKey(1)
        )

        assert(
            view19At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isEnemy(5)
        )
    }
}