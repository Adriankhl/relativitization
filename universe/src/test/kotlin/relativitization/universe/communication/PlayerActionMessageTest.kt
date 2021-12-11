package relativitization.universe.communication

import relativitization.universe.data.commands.ChangeVelocityCommand
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.data.components.defaults.physics.Velocity
import relativitization.universe.data.serializer.DataSerializer
import kotlin.test.Test

internal class PlayerActionMessageTest {
    @Test
    fun serialize() {
        val message = PlayerActionMessage(
            1,
            "abc",
            listOf(
                ChangeVelocityCommand(
                    targetVelocity = Velocity(0.3, 0.3, 0.3),
                    toId = 1,
                    fromId = 1,
                    fromInt4D = Int4D(0, 0, 0, 0),
                )
            )
        )

        val message2: PlayerActionMessage = DataSerializer.copy(message)
        assert(message2.password == "abc")
    }
}