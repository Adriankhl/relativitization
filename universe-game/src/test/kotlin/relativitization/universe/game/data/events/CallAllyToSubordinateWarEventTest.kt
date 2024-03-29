package relativitization.universe.game.data.events

import kotlinx.coroutines.runBlocking
import relativitization.universe.core.Universe
import relativitization.universe.core.data.MutableUniverseSettings
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.GameUniverseInitializer
import relativitization.universe.game.data.commands.AddEventCommand
import relativitization.universe.game.data.commands.DeclareWarCommand
import relativitization.universe.game.data.commands.DefaultCommandAvailability
import relativitization.universe.game.data.commands.SelectEventChoiceCommand
import relativitization.universe.game.data.components.defaults.diplomacy.hasAllySubordinateWar
import relativitization.universe.game.data.components.defaults.diplomacy.hasSubordinateWar
import relativitization.universe.game.data.components.defaults.diplomacy.isAlly
import relativitization.universe.game.data.components.defaults.diplomacy.isEnemy
import relativitization.universe.game.data.components.diplomacyData
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import relativitization.universe.game.global.DefaultGlobalMechanismList
import relativitization.universe.game.mechanisms.DefaultMechanismLists
import kotlin.test.Test

internal class CallAllyToSubordinateWarEventTest {
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

        val event = ProposeAllianceEvent(
            toId = 3,
        )

        val proposeAllianceCommand = AddEventCommand(
            event,
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

        runBlocking {
            universe.postProcessUniverse(
                mapOf(),
                mapOf(
                    3 to listOf(
                        SelectEventChoiceCommand(
                            toId = 3,
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
                                subordinateId = 2,
                                warTargetId = 5,
                            ),
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

        runBlocking {
            universe.postProcessUniverse(
                mapOf(),
                mapOf(
                    3 to listOf(
                        SelectEventChoiceCommand(
                            toId = 3,
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