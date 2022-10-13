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
) : DefaultCommand() {
    override fun name(): String = "Nothing"

    override fun description(fromId: Int): I18NString = I18NString(
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

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) { }
}

/**
 * For informing that the proposed command cannot be sent
 */
@Serializable
data class CannotSendCommand(
    override val toId: Int = -1,
    val reason: I18NString,
) : DefaultCommand() {
    override fun name(): String = "Something Wrong"

    override fun description(fromId: Int): I18NString = reason

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(false)

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(true)

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) { }
}

/**
 * For informing that the proposed command might not be executed
 */
@Serializable
data class ExecuteWarningCommand(
    override val toId: Int = -1,
    val reason: I18NString,
) : DefaultCommand() {
    override fun name(): String = "Warning"

    override fun description(fromId: Int): I18NString = reason

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(false)

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(true)

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) { }
}