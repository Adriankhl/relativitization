package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.maths.physics.Int4D
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
    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        return CommandErrorMessage(false)
    }

    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): CommandErrorMessage {
        val isNotLeader = CommandErrorMessage(
            !playerData.isLeaderOrSelf(fromId),
            I18NString("Is not leader of self. ")
        )

        return CommandErrorMessage(
            listOf(
                isNotLeader
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.addDirectSubordinateId(fromId)
    }
}