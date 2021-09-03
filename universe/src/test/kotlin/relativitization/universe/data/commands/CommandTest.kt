package relativitization.universe.data.commands

import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.data.component.physics.Velocity
import kotlin.test.Test

internal class CommandTest {
    @Test
    fun reflectionTest() {
        val c1: ChangeVelocityCommand = ChangeVelocityCommand(
            targetVelocity = Velocity(0.1, 0.0, 0.0),
            toId = 2,
            fromId = 1,
            fromInt4D = Int4D(7, 0, 0, 0),
        )

        val c2: Command = c1

        assert(c1.name() == c2.name())
    }

    @Test
    fun repeatedNameTest() {
        assert(CommandCollection.defaultCommandList.groupingBy { it }.eachCount().any { it.value == 1 })
    }
}