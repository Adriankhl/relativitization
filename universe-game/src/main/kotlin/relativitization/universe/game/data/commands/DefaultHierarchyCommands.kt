package relativitization.universe.game.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.CommandErrorMessage
import relativitization.universe.core.maths.physics.Int4D
import relativitization.universe.core.utils.I18NString

/**
 * Add this player as direct subordinate of another player
 * For mechanism only
 */
@Serializable
data class AddDirectSubordinateCommand(
    override val toId: Int,
) : DefaultCommand() {
    override fun name(): String = "Add Direct Subordinate"
    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        return CommandErrorMessage(false)
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
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

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        playerData.addDirectSubordinateId(fromId)
    }
}