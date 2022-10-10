package relativitization.universe.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.maths.physics.Int4D
import relativitization.universe.data.events.MoveToDouble3DEvent
import relativitization.universe.data.events.name
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.testing.TestingFixedMinimal
import kotlin.test.Test

internal class SelectEventChoiceCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimal.name(),
                )
            )
        )
        val view1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(2)
        assert(view1.get(2).int4D == Int4D(6, 0, 0, 0))

        val event = MoveToDouble3DEvent(
            toId = 2,
            fromId = 1,
            targetDouble3D = view1.get(3).groupCenterDouble3D(view1.universeSettings.groupEdgeLength),
            maxSpeed = 0.2

        )

        val addEventCommand = AddEventCommand(
            event,
            view1.getCurrentPlayerData().int4D
        )

        // Add Command
        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(addEventCommand)
                ),
                mapOf(
                    2 to listOf(),
                    3 to listOf(),
                    4 to listOf()
                )
            )
            universe.preProcessUniverse()
        }

        val view2: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(2)

        val selectEventChoiceCommand = SelectEventChoiceCommand(
            toId = view2.id,
            fromId = view2.id,
            fromInt4D = view2.getCurrentPlayerData().int4D,
            eventKey = 0,
            eventName = view2.getCurrentPlayerData().playerInternalData.eventDataMap.getValue(0).event.name(),
            choice = 1
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf()
                ),
                mapOf(
                    2 to listOf(selectEventChoiceCommand),
                    3 to listOf(),
                    4 to listOf()
                )
            )
            universe.preProcessUniverse()
        }

        val view3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(2)

        assert(view3.getCurrentPlayerData().playerInternalData.eventDataMap.isEmpty())
    }
}