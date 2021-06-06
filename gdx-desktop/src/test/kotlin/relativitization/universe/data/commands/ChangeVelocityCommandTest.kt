package relativitization.universe.data.commands

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.physics.Int4D
import relativitization.universe.data.physics.Velocity
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverse

internal class ChangeVelocityCommandTest {
    @Test
    fun fixMinimalTest() {
        val universe = Universe(GenerateUniverse.generate(GenerateSettings()))
        val view7: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)
        assert(view7.get(2).int4D == Int4D(7, 0, 0, 0))

        val command = ChangeVelocityCommand(1, 2, Int4D(7, 0, 0, 0), Velocity(0.1, 0.0, 0.0))

        runBlocking {
            universe.postProcessUniverse(mapOf(1 to listOf(command)), mapOf(2 to listOf(), 3 to listOf()))
            universe.preProcessUniverse()
        }

        val view8: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        // Since player 2 left player 1, player 1 can only see the historical image which is at t = 6
        assert(view8.get(2).int4D == Int4D(6, 0, 0, 0))
    }
}