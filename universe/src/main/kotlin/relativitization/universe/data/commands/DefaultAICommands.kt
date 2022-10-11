package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.aiData
import relativitization.universe.maths.physics.Int4D


/**
 * Indicate that a command has been sent to a player
 *
 * @property recentCommandPlayerId the id of the player that this player has sent
 */
@Serializable
data class AddRecentCommandTimeCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
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

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.aiData().recentCommandTimeMap[recentCommandPlayerId] =
            playerData.int4D.t
    }

}