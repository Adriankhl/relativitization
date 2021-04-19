package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.Command
import relativitization.universe.data.physics.Int4D

@Serializable
data class ChangeVelocityCommand(
    override val fromId: Int,
    override val toId: Int,
    override val fromInt4D: Int4D,
) : Command() {
    override fun execute(playerData: PlayerData): List<Command> {
        TODO("Not yet implemented")
    }
}