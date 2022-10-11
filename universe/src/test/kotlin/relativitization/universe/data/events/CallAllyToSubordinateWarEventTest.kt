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
import relativitization.universe.generate.testing.TestingFixedMinimal
import kotlin.test.Test

internal class CallAllyToSubordinateWarEventTest {
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
        val view3At5: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(5)

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
                mapOf(),
                mapOf(
                    5 to listOf(
                        DeclareWarCommand(
                            toId = 2,
                            fromId = 5,
                            fromInt4D = view3At5.getCurrentPlayerData().int4D
                        )
                    )
                ),
            )
            universe.preProcessUniverse()
        }

        runBlocking {
            for (i in (1..3)) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }

        val view4At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        assert(
            view4At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isEnemy(5)
        )

        assert(
            view4At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .hasSubordinateWar(2, 5)
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(
                        AddEventCommand(
                            CallAllyToSubordinateWarEvent(
                                toId = 3,
                                fromId = 1,
                                subordinateId = 2,
                                warTargetId = 5,
                            ),
                            fromInt4D = view4At1.getCurrentPlayerData().int4D,
                        )
                    )
                ),
                mapOf(),
            )
            universe.preProcessUniverse()
        }

        runBlocking {
            for (i in (1..3)) {
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
                            choice = 0
                        )
                    )
                ),
            )
            universe.preProcessUniverse()
        }

        val view6At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        val view6At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)
        val view6At5: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(5)

        assert(
            view6At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isEnemy(5)
        )

        assert(
            view6At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isAlly(1)
        )

        assert(
            view6At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isEnemy(5)
        )

        assert(
            view6At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .hasAllySubordinateWar(1, 2, 5)
        )

        assert(
            view6At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .enemyIdSet == setOf(1, 2)
        )

        runBlocking {
            for (i in (1..5)) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }


        val view7At1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        val view7At3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(3)
        val view7At5: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(5)

        assert(
            view7At1.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isEnemy(5)
        )

        assert(
            view7At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isAlly(1)
        )

        assert(
            view7At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .isEnemy(5)
        )

        assert(
            view7At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .hasAllySubordinateWar(1, 2, 5)
        )

        assert(
            view7At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .enemyIdSet == setOf(1, 2, 3)
        )
    }
}