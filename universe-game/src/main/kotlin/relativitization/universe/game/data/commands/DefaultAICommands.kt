package relativitization.universe.game.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.CommandErrorMessage
import relativitization.universe.core.data.commands.CommandI18NStringFactory
import relativitization.universe.core.maths.physics.Int4D
import relativitization.universe.game.data.components.aiData


/**
 * Indicate that a command has been sent to a player
 *
 * @property recentCommandPlayerId the id of the player that this player has sent
 */
@Serializable
data class AddRecentCommandTimeCommand(
    override val toId: Int,
    val recentCommandPlayerId: Int,
) : DefaultCommand() {
    override fun name(): String = "Add Recent Command Time"

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, toId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
            )
        )
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.aiData().recentCommandTimeMap[recentCommandPlayerId] =
            playerData.int4D.t
    }

}