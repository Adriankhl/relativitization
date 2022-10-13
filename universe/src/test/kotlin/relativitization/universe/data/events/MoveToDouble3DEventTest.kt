package relativitization.universe.data.events

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.commands.SelectEventChoiceCommand
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.testing.TestingFixedMinimal
import relativitization.universe.maths.physics.Double4D
import relativitization.universe.maths.physics.Int4D
import kotlin.test.Test

internal class MoveToDouble3DEventTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimal.name(),
                )
            )
        )
        val view1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        assert(view1.get(2).int4D == Int4D(6, 0, 0, 0))

        val event = MoveToDouble3DEvent(
            toId = 2,
            targetDouble3D = view1.get(3)
                .groupCenterDouble3D(view1.universeSettings.groupEdgeLength),
            maxSpeed = 0.2

        )

        val command = AddEventCommand(
            event,
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
            universe.postProcessUniverse(
                mapOf(),
                mapOf(
                    2 to listOf(
                        SelectEventChoiceCommand(
                            toId = 2,
                            eventKey = 0,
                            choice = 0,
                        )
                    )
                )
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

        val view3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        assert(
            view3.get(2).int4D.toInt3D() == view3.get(3).int4D.toInt3D()
        )
        assert(
            view3.get(2).double4D.toDouble3D() == view3.get(3)
                .groupCenterDouble3D(view3.universeSettings.groupEdgeLength)
        )
    }

    @Test
    fun fixedMinimalStrangeCaseTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimal.name(),
                )
            )
        )
        val view1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        assert(view1.getCurrentPlayerData().int4D == Int4D(6, 0, 0, 0))

        val originalDouble4D: Double4D = view1.getCurrentPlayerData().double4D

        val event = MoveToDouble3DEvent(
            toId = 1,
            targetDouble3D = view1.get(3)
                .groupCenterDouble3D(view1.universeSettings.groupEdgeLength),
            maxSpeed = 0.2

        )

        val command = AddEventCommand(
            event,
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

        val view2: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        assert(view2.getCurrentPlayerData().playerInternalData.eventDataMap.isEmpty())

        runBlocking {
            for (i in 1..20) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }
        val view3: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        assert(view3.getCurrentPlayerData().double4D.toDouble3D() == originalDouble4D.toDouble3D())
    }
}