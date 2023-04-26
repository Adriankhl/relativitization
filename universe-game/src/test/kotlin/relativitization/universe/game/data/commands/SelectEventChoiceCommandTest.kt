package relativitization.universe.game.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.core.Universe
import relativitization.universe.core.data.MutableUniverseSettings
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethodCollection
import relativitization.universe.core.maths.physics.Int4D
import relativitization.universe.game.GameUniverseInitializer
import relativitization.universe.game.data.events.MoveToDouble3DEvent
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import relativitization.universe.game.global.DefaultGlobalMechanismList
import relativitization.universe.game.mechanisms.DefaultMechanismLists
import kotlin.test.Test

internal class SelectEventChoiceCommandTest {
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
        val view1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(2)
        assert(view1.get(2).int4D == Int4D(6, 0, 0, 0))

        val event = MoveToDouble3DEvent(
            toId = 2,
            targetDouble3D = view1.get(3).groupCenterDouble3D(view1.universeSettings.groupEdgeLength),
            maxSpeed = 0.2

        )

        val addEventCommand = AddEventCommand(
            event,
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
            eventKey = 0,
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