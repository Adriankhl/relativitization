package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.maths.physics.Int4D
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.NormalString


@Serializable
data class DummyCommand(
    override val toId: Int = -1,
    override val fromId: Int = -1,
    override val fromInt4D: Int4D = Int4D(0, 0, 0, 0),
) : DefaultCommand() {

    override fun description(): I18NString = I18NString(
        listOf(NormalString("Do nothing")),
        listOf(),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        return CommandErrorMessage(
            false,
            I18NString("This is dummy. ")
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) { }
}

/**
 * For informing that the proposed command cannot be sent
 */
@Serializable
data class CannotSendCommand(
    override val toId: Int = -1,
    override val fromId: Int = -1,
    override val fromInt4D: Int4D = Int4D(0, 0, 0, 0),
    val reason: I18NString,
) : DefaultCommand() {

    override fun description(): I18NString = reason

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(false)

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(true)

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) { }
}

/**
 * For informing that the proposed command might not be executed
 */
@Serializable
data class ExecuteWarningCommand(
    override val toId: Int = -1,
    override val fromId: Int = -1,
    override val fromInt4D: Int4D = Int4D(0, 0, 0, 0),
    val reason: I18NString,
) : DefaultCommand() {

    override fun description(): I18NString = reason

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(false)

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(true)

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) { }
}