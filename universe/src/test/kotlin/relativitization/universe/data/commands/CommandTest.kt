package relativitization.universe.data.commands

import relativitization.universe.maths.physics.Int4D
import relativitization.universe.maths.physics.Velocity
import kotlin.test.Test

internal class CommandTest {
    @Test
    fun reflectionTest() {
        val c1: ChangeVelocityCommand = ChangeVelocityCommand(
            toId = 2,
            fromId = 1,
            fromInt4D = Int4D(7, 0, 0, 0),
            targetVelocity = Velocity(0.1, 0.0, 0.0),
        )

        val c2: Command = c1

        assert(c1.name() == c2.name())
    }
}