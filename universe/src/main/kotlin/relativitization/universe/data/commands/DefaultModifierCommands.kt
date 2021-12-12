package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.NormalString

@Serializable
data class DisableFuelIncreaseCommand(
    val disableFuelIncreaseTimeLimit: Int,
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
) : DefaultCommand() {

    override val description: I18NString = I18NString(
        listOf(
            NormalString("Disable fuel increase for "),
            IntString(0),
            NormalString(" turn"),
        ),
        listOf(
            disableFuelIncreaseTimeLimit.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandSuccessMessage {
        val isSelf = CommandSuccessMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
        )

        return CommandSuccessMessage(
            listOf(
                isSelf
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return playerData.playerId == fromId
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.modifierData().physicsModifierData.disableFuelIncreaseByTime(
            disableFuelIncreaseTimeLimit
        )
    }
}