package relativitization.universe.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.maths.physics.Int4D
import relativitization.universe.data.events.MoveToDouble3DEvent
import relativitization.universe.data.events.name
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.generate.method.testing.TestingFixedMinimal
import kotlin.test.Test

internal class SelectEventChoiceCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(GenerateUniverseMethodCollection.generate(GenerateSettings(
            generateMethod = TestingFixedMinimal.name(),
        )), ".")
        val view6: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(2)
        assert(view6.get(2).int4D == Int4D(6, 0, 0, 0))

        val event = MoveToDouble3DEvent(
            toId = 2,
            fromId = 2,
            stayTime = 999,
            targetDouble3D = view6.get(3).groupCenterDouble3D(view6.universeSettings.groupEdgeLength),
            maxSpeed = 0.2

        )

        val addEventCommand = AddEventCommand(
            event,
            view6.getCurrentPlayerData().int4D
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

        val view7: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(2)

        val selectEventChoiceCommand = SelectEventChoiceCommand(
            toId = view7.id,
            fromId = view7.id,
            fromInt4D = view7.getCurrentPlayerData().int4D,
            eventKey = 0,
            eventName = view7.getCurrentPlayerData().playerInternalData.eventDataMap.getValue(0).event.name(),
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

        val view8: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(2)

        assert(view8.getCurrentPlayerData().playerInternalData.eventDataMap.isEmpty())
    }
}