package relativitization.universe.communication

import relativitization.universe.data.commands.ChangeVelocityCommand
import relativitization.universe.data.physics.Int4D
import relativitization.universe.data.physics.Velocity
import relativitization.universe.data.serializer.DataSerializer
import kotlin.test.Test

internal class CommandInputMessageTest {
    @Test
    fun serialize() {
        val message = CommandInputMessage(
            1,
            "abc",
            listOf(ChangeVelocityCommand(
                Velocity(0.3, 0.3, 0.3),
                1,
                Int4D(0,0,0,0),
                1,
            ))
        )

        val message2: CommandInputMessage= DataSerializer.copy(message)
        assert(message2.password == "abc")
    }
}