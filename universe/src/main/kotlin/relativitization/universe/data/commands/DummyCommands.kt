package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.physics.Int4D


@Serializable
data class DummyCommand(
    override val toId: Int = -1,
    override val fromId: Int = -1,
    override val fromInt4D: Int4D = Int4D(0, 0, 0, 0),
) : Command() {
    override val name: CommandName = CommandName.DUMMY

    override val description = "Do nothing"

    override fun canSend(playerData: PlayerData, universeSettings: UniverseSettings): Boolean {
        return true
    }

    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return true
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
    }
}

/**
 * For informing that the proposed command cannot be sent
 */
@Serializable
data class CannotSendCommand(
    override val toId: Int = -1,
    override val fromId: Int = -1,
    override val fromInt4D: Int4D = Int4D(0, 0, 0, 0),
) : Command() {
    override val name: CommandName = CommandName.CANNOT_SEND

    override val description: String = "Cannot send this command"

    override fun canSend(playerData: PlayerData, universeSettings: UniverseSettings): Boolean {
        return true
    }

    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return true
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
    }
}