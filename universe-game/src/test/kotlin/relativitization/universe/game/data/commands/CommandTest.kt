package relativitization.universe.game.data.commands

import relativitization.universe.game.data.commands.ChangeVelocityCommand
import relativitization.universe.game.data.commands.Command
import relativitization.universe.game.maths.physics.Velocity
import kotlin.test.Test

internal class CommandTest {
    @Test
    fun reflectionTest() {
        val c1 = ChangeVelocityCommand(
            toId = 2,
            targetVelocity = Velocity(0.1, 0.0, 0.0),
        )

        val c2: Command = c1

        assert(c1.name() == c2.name())
    }
}