package relativitization.universe.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.data.events.MoveToDouble3DEvent
import relativitization.universe.data.events.name
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverse
import relativitization.universe.generate.UniverseGenerationCollection
import kotlin.test.Test

internal class SelectEventChoiceCommandTest {
    @Test
    fun fixMinimalTest() {
        val universe = Universe(UniverseGenerationCollection.generate(GenerateSettings()), ".")
        val view7: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(2)
        assert(view7.get(2).int4D == Int4D(7, 0, 0, 0))

        val event = MoveToDouble3DEvent(
            toId = 2,
            fromId = 2,
            targetDouble3D = view7.get(3)
                .groupCenterDouble3D(view7.universeSettings.groupEdgeLength),
            maxSpeed = 0.2,
            stayTime = 999

        )

        val addEventCommand = AddEventCommand(
            event,
            Int4D(7, 0, 0, 0),
        )

        // Add Command
        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf()
                ),
                mapOf(
                    2 to listOf(addEventCommand),
                    3 to listOf(),
                    4 to listOf()
                )
            )
            universe.preProcessUniverse()
        }

        val view8: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(2)

        val selectEventChoiceCommand = SelectEventChoiceCommand(
            eventKey = 0,
            eventName = view8.getCurrentPlayerData().playerInternalData.eventDataMap.getValue(0).event.name(),
            choice = 1,
            fromId = view8.id,
            fromInt4D = view8.getCurrentPlayerData().int4D,
            toId = view8.id

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

        val view9: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(2)

        assert(view9.getCurrentPlayerData().playerInternalData.eventDataMap.isEmpty())
    }
}