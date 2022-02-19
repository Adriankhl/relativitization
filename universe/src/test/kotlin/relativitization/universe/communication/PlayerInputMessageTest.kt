package relativitization.universe.communication

import relativitization.universe.data.commands.ChangeVelocityCommand
import relativitization.universe.maths.physics.Int4D
import relativitization.universe.maths.physics.Velocity
import relativitization.universe.data.serializer.DataSerializer
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
                    fromId = 1,
                    fromInt4D = Int4D(0, 0, 0, 0),
                    targetVelocity = Velocity(0.3, 0.3, 0.3),
                )
            )
        )

        val message2: PlayerInputMessage = DataSerializer.copy(message)
        assert(message2.password == "abc")
    }
}