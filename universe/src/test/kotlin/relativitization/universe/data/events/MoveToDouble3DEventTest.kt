package relativitization.universe.data.events

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.components.defaults.physics.Double4D
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import kotlin.test.Test

internal class MoveToDouble3DEventTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(GenerateUniverseMethodCollection.generate(GenerateSettings()), ".")
        val view7: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        assert(view7.get(2).int4D == Int4D(7, 0, 0, 0))

        val event = MoveToDouble3DEvent(
            toId = 2,
            fromId = 1,
            stayTime = 999,
            targetDouble3D = view7.get(3)
                .groupCenterDouble3D(view7.universeSettings.groupEdgeLength),
            maxSpeed = 0.2

        )

        val command = AddEventCommand(
            event,
            Int4D(7, 0, 0, 0),
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(command)
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }


        runBlocking {
            for (i in 1..20) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }

        val view: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        assert(
            view.get(2).double4D.toDouble3D() == view.get(3)
                .groupCenterDouble3D(view.universeSettings.groupEdgeLength)
        )
    }

    @Test
    fun fixedMinimalStrangeCaseTest() {
        val universe = Universe(GenerateUniverseMethodCollection.generate(GenerateSettings()), ".")
        val view7: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        assert(view7.get(1).int4D == Int4D(7, 0, 0, 0))

        val originalDouble4D: Double4D = view7.get(1).double4D

        val event = MoveToDouble3DEvent(
            toId = 1,
            fromId = 1,
            stayTime = 999,
            targetDouble3D = view7.get(3)
                .groupCenterDouble3D(view7.universeSettings.groupEdgeLength),
            maxSpeed = 0.2

        )

        val command = AddEventCommand(
            event,
            Int4D(7, 0, 0, 0),
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(command)
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        val view8: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        assert(view8.get(1).playerInternalData.eventDataMap.isEmpty())

        runBlocking {
            for (i in 1..20) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }
        val finalView: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        // Moved a little
        assert(finalView.get(1).double4D.toDouble3D() != originalDouble4D.toDouble3D())
    }
}