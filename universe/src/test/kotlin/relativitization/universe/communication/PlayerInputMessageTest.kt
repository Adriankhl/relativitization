package relativitization.universe.communication

import relativitization.universe.data.commands.ChangeVelocityCommand
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.maths.physics.Velocity
import kotlin.test.Test

internal class PlayerInputMessageTest {
    @Test
    fun serialize() {
        val message = PlayerInputMessage(
            1,
            "abc",
            listOf(
                ChangeVelocityCommand(
                    toId = 1,
                    targetVelocity = Velocity(0.3, 0.3, 0.3),
                )
            )
        )

        val message2: PlayerInputMessage = DataSerializer.copy(message)
        assert(message2.password == "abc")
    }
}