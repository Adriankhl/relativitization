package relativitization.universe.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.events.ProposePeaceEvent
import relativitization.universe.data.events.name
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.generate.method.testing.TestingFixedMinimal
import relativitization.universe.maths.physics.Int3D
import relativitization.universe.maths.physics.Int4D
import kotlin.test.Test

internal class ProposePeaceCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimal.name(),
                )
            )
        )

        val view6At3 = universe.getUniverse3DViewAtPlayer(3)
        val view6At5 = universe.getUniverse3DViewAtPlayer(5)

        assert(
            view6At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )
        assert(
            view6At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    5 to listOf(
                        DeclareWarCommand(
                            toId = 3,
                            fromId = 5,
                            fromInt4D = view6At5.getCurrentPlayerData().int4D,
                        )
                    )
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        val view8At3 = universe.getUniverse3DViewAtPlayer(3)
        val view8At5 = universe.getUniverse3DViewAtPlayer(5)



        assert(
            view8At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )
        assert(
            view8At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.keys == setOf(3)
        )

        runBlocking {
            for (i in (1..2)) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }


        val view10At3 = universe.getUniverse3DViewAtPlayer(3)
        val view10At5 = universe.getUniverse3DViewAtPlayer(5)


        assert(
            view10At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.keys == setOf(5)
        )
        assert(
            view10At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.keys == setOf(3)
        )

        val proposePeaceEvent = ProposePeaceEvent(
            toId = 5,
            fromId = 3,
        )
        val addEventCommand = AddEventCommand(
            proposePeaceEvent,
            fromInt4D = view10At3.getCurrentPlayerData().int4D,
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    3 to listOf(addEventCommand),
                ),
                mapOf()
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

        val view13At5 = universe.getUniverse3DViewAtPlayer(5)

        assert(
            view13At5.getCurrentPlayerData().playerInternalData.eventDataMap.isNotEmpty()
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    5 to listOf(
                        SelectEventChoiceCommand(
                            toId = 5,
                            fromId = 5,
                            fromInt4D = view13At5.getCurrentPlayerData().int4D,
                            eventKey = 0,
                            eventName = ProposePeaceEvent::class.name(),
                            choice = 0
                        )
                    ),
                ),
                mapOf()
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


        val viewFinalAt3 = universe.getUniverse3DViewAtPlayer(3)
        val viewFinalAt5 = universe.getUniverse3DViewAtPlayer(5)

        assert(
            viewFinalAt3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )
        assert(
            viewFinalAt5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )

    }
}