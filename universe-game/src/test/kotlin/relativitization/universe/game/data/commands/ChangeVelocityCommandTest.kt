package relativitization.universe.game.data.commands


import kotlinx.coroutines.runBlocking
import relativitization.universe.game.Universe
import relativitization.universe.game.data.UniverseData3DAtPlayer
import relativitization.universe.game.generate.GenerateSettings
import relativitization.universe.game.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import relativitization.universe.game.maths.physics.Int4D
import relativitization.universe.game.maths.physics.Velocity
import kotlin.test.Test

internal class ChangeVelocityCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimalGenerate.name(),
                )
            )
        )
        val view1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        assert(view1.get(2).int4D == Int4D(6, 0, 0, 0))

        val command = ChangeVelocityCommand(
            toId = 2,
            targetVelocity = Velocity(0.1, 0.0, 0.0),
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

        // Since player 2 left player 1, player 1 can only see the after image which is at t = 7
        assert(view2.get(2).int4D == Int4D(6, 0, 0, 0))
    }
}