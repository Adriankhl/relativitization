package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.physics.Int4D
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.RealString

@Serializable
data class DisableFuelIncreaseCommand(
    val disableFuelIncreaseTimeLimit: Int,
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
) : Command() {

    override val description: I18NString = I18NString(
        listOf(
            RealString("Disable fuel increase for "),
            IntString(0),
            RealString(" turn"),
        ),
        listOf(
            disableFuelIncreaseTimeLimit.toString(),
        ),
    )

    override fun canSend(playerData: MutablePlayerData, universeSettings: UniverseSettings): CanSendCheckMessage {
        val sameId: Boolean = playerData.playerId == toId
        return if (sameId) {
            CanSendCheckMessage(true)
        } else {
            CanSendCheckMessage(
                false,
                CanSendWIthMessageI18NStringFactory.isNotToSelf(playerData.playerId, toId)
            )
        }
    }

    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.playerId == fromId
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.modifierData().physicsModifierData.disableFuelIncreaseByTime(
            disableFuelIncreaseTimeLimit
        )
    }
}