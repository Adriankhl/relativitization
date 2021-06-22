package relativitization.universe.data.events

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.commands.ChangeVelocityCommand
import relativitization.universe.data.physics.Double3D
import relativitization.universe.data.physics.Int4D
import relativitization.universe.data.physics.Velocity
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverse
import kotlin.test.Test

internal class MoveToDouble3DEventTest {
    @Test
    fun fixMinimalTest() {
        val universe = Universe(GenerateUniverse.generate(GenerateSettings()), ".")
        val view7: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        assert(view7.get(2).int4D == Int4D(7, 0, 0, 0))

        val event = MoveToDouble3DEvent(
            playerId = 2,
            targetDouble3D = view7.get(3)
                .groupCenterDouble3D(view7.universeSettings.groupEdgeLength),
            maxSpeed = 0.2,
            stayTime = 999

        )

        val command = AddEventCommand(
            event,
            1,
            Int4D(7, 0, 0, 0),
            2,
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(command)
                ),
                mapOf(
                    2 to listOf(),
                    3 to listOf(),
                    4 to listOf()
                )
            )
            universe.preProcessUniverse()
        }


        runBlocking {
            for (i in 1..20) {
                universe.postProcessUniverse(
                    mapOf(
                        1 to listOf()
                    ),
                    mapOf(
                        2 to listOf(),
                        3 to listOf(),
                    )
                )
                universe.preProcessUniverse()
            }
        }

        val view: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        assert(view.get(2).double4D.toDouble3D() == view.get(3).groupCenterDouble3D(view.universeSettings.groupEdgeLength))
    }
}