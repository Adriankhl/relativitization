package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.physics.Int4D
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.RealString

@Serializable
data class DeclareWarCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D
) : Command() {
    override val description: I18NString = I18NString(
        listOf(
            RealString("Declare war on "),
            IntString(0),
        ),
        listOf(
            toId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        val isNotLeader: Boolean = !playerData.isLeaderOrSelf(toId)
        val isNotLeaderI18NString: I18NString = if (isNotLeader) {
            I18NString("")
        } else {
            I18NString("Target is leader")
        }

        val isNotSubordinate: Boolean = !playerData.isLeaderOrSelf(toId)
        val isNotSubordinateI18NString: I18NString = if (isNotSubordinate) {
            I18NString("")
        } else {
            I18NString("Target is leader")
        }

        return CanSendCheckMessage(
            isNotLeader && isNotSubordinate,
            I18NString.combine(
                listOf(
                    isNotLeaderI18NString,
                    isNotSubordinateI18NString
                )
            )
        )
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {

    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        TODO("Not yet implemented")
    }
}