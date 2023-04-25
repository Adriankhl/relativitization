package relativitization.universe.game.communication

import relativitization.universe.game.data.commands.ChangeVelocityCommand
import relativitization.universe.game.data.serializer.DataSerializer
import relativitization.universe.game.communication.PlayerInputMessage
import relativitization.universe.game.maths.physics.Velocity
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