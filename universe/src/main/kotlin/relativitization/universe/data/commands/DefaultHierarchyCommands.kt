package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.utils.I18NString

/**
 * Add this player as direct subordinate of another player
 * For mechanism only
 */
@Serializable
data class AddDirectSubordinateCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D
) : DefaultCommand() {
    override val description: I18NString = I18NString("")

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        return CommandErrorMessage(false)
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        return CommandErrorMessage(true)
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.addDirectSubordinateId(fromId)
    }
}