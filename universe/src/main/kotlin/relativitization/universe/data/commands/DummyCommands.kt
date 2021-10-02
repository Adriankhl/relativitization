package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.RealString


@Serializable
data class DummyCommand(
    override val toId: Int = -1,
    override val fromId: Int = -1,
    override val fromInt4D: Int4D = Int4D(0, 0, 0, 0),
) : Command() {

    override val description: I18NString = I18NString(
        listOf(RealString("Do nothing")),
        listOf(),
    )

    override fun canSend(playerData: MutablePlayerData, universeSettings: UniverseSettings): CanSendCheckMessage {
        return CanSendCheckMessage(
            false,
            I18NString("This is dummy. ")
        )
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
    val reason: I18NString,
    override val toId: Int = -1,
    override val fromId: Int = -1,
    override val fromInt4D: Int4D = Int4D(0, 0, 0, 0),
) : Command() {

    override val description: I18NString = reason

    override fun canSend(playerData: MutablePlayerData, universeSettings: UniverseSettings): CanSendCheckMessage {
        return CanSendCheckMessage(false)
    }

    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return true
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
    }
}