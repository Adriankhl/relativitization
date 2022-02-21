package relativitization.universe.data.events

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.maths.physics.Double4D
import relativitization.universe.maths.physics.Int4D
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.generate.method.testing.TestingFixedMinimal
import kotlin.test.Test

internal class MoveToDouble3DEventTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(GenerateUniverseMethodCollection.generate(GenerateSettings(
            generateMethod = TestingFixedMinimal.name(),
        )), ".")
        val view6: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        assert(view6.get(2).int4D == Int4D(7, 0, 0, 0))

        val event = MoveToDouble3DEvent(
            toId = 2,
            fromId = 1,
            stayTime = 999,
            targetDouble3D = view6.get(3)
                .groupCenterDouble3D(view6.universeSettings.groupEdgeLength),
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

        val finalView: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        assert(
            finalView.get(2).double4D.toDouble3D() == finalView.get(3)
                .groupCenterDouble3D(finalView.universeSettings.groupEdgeLength)
        )
    }

    @Test
    fun fixedMinimalStrangeCaseTest() {
        val universe = Universe(GenerateUniverseMethodCollection.generate(GenerateSettings(
            generateMethod = TestingFixedMinimal.name(),
        )), ".")
        val view6: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        assert(view6.getCurrentPlayerData().int4D == Int4D(7, 0, 0, 0))

        val originalDouble4D: Double4D = view6.getCurrentPlayerData().double4D

        val event = MoveToDouble3DEvent(
            toId = 1,
            fromId = 1,
            stayTime = 999,
            targetDouble3D = view6.get(3)
                .groupCenterDouble3D(view6.universeSettings.groupEdgeLength),
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

        val view7: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        assert(view7.getCurrentPlayerData().playerInternalData.eventDataMap.isEmpty())

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
        assert(finalView.getCurrentPlayerData().double4D.toDouble3D() == originalDouble4D.toDouble3D())
    }
}