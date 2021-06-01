package relativitization.universe.communication

import org.junit.jupiter.api.Assertions.*
import relativitization.universe.data.commands.ChangeVelocityCommand
import relativitization.universe.data.commands.Command
import relativitization.universe.data.physics.Int4D
import relativitization.universe.data.physics.Velocity
import relativitization.universe.data.serializer.DataSerializer
import kotlin.test.Test

internal class CommandInputMessageTest {
    @Test
    fun serialize() {
        //val message = CommandInputMessage(1, "abc", listOf(ChangeVelocityCommand(1, 1, Int4D(0,0,0,0),Velocity(0.3, 0.3, 0.3))))
        val c: Command = ChangeVelocityCommand(1, 1, Int4D(0,0,0,0),Velocity(0.3, 0.3, 0.3))
        println(DataSerializer.encode(c))
    }
}