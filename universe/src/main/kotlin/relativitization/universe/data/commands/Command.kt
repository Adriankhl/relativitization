package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.maths.physics.Int4D
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.NormalString
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.reflect.KClass

@Serializable
sealed class Command {
    // The id of the player to receive this command
    abstract val toId: Int

    // The id of the player who send this command
    abstract val fromId: Int

    // The int4D coordinates of the player who send this command
    abstract val fromInt4D: Int4D

    /**
     * Description of the command, default to empty description
     */
    open fun description(): I18NString = I18NString("")


    /**
     * Check to see if fromId match
     */
    private fun checkFromId(playerData: MutablePlayerData): Boolean {
        return if (playerData.playerId == fromId) {
            true
        } else {
            val className = this::class.qualifiedName
            logger.error("${className}: player id not equal to command from id")
            false
        }
    }

    /**
     * Check to see if toId match
     */
    private fun checkToId(playerData: MutablePlayerData): Boolean {
        return if (playerData.playerId == toId) {
            true
        } else {
            val className = this::class.qualifiedName
            logger.error("${className}: player id not equal to command target id")
            false
        }
    }


    /**
     * Check if the player (sender) can send the command, default to always true
     *
     * @param playerData the data of the player to send this command
     * @param universeSettings the universe settings
     */
    protected open fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(true)

    /**
     * Check if the universe has this command, and it can be sent by the player
     *
     * @param playerData the player data to send this command
     */
    fun canSendFromPlayer(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val hasCommand = CommandErrorMessage(
            CommandCollection.hasCommand(universeSettings, this),
            I18NString(
                listOf(
                    NormalString("No such command: "),
                    IntString(0),
                    NormalString(". ")
                ),
                listOf(
                    this.toString()
                ),
            )
        )

        val isFromIdValid = CommandErrorMessage(
            checkFromId(playerData),
            I18NString(
                listOf(
                    NormalString("Player id "),
                    IntString(0),
                    NormalString(" is not the same as the id "),
                    IntString(1),
                    NormalString(" in this command. ")
                ),
                listOf(
                    playerData.playerId.toString(),
                    fromId.toString(),
                ),
            )
        )

        val isFromInt4DValid = CommandErrorMessage(
            playerData.int4D.toInt4D() == fromInt4D,
            I18NString(
                listOf(
                    NormalString("Player coordinate "),
                    IntString(0),
                    NormalString(" is not the same as the coordinate "),
                    IntString(1),
                    NormalString(" in this command. ")
                ),
                listOf(
                    playerData.int4D.toInt4D().toString(),
                    fromInt4D.toString(),
                ),
            )
        )

        val canSendErrorMessage: CommandErrorMessage = canSend(playerData, universeSettings)

        return CommandErrorMessage(
            listOf(
                hasCommand,
                isFromIdValid,
                isFromInt4DValid,
                canSendErrorMessage,
            )
        )
    }

    /**
     * Execute on self in order to end this command
     */
    protected open fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
    }

    /**
     * Check and self execute
     */
    fun checkAndSelfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val sendMessage: CommandErrorMessage = canSendFromPlayer(playerData, universeSettings)

        if (sendMessage.success) {
            try {
                selfExecuteBeforeSend(playerData, universeSettings)
            } catch (e: Throwable) {
                logger.error("checkAndSelfExecuteBeforeSend fail, throwable $e")
                throw e
            }
        } else {
            val className = this::class.qualifiedName
            logger.debug("$className cannot be sent by $fromId: ${sendMessage.errorMessage.toNormalString()}")
        }

        return sendMessage
    }


    /**
     * Check if the player can receive the command, default to always true
     *
     * @param playerData the data of the player to execute this command
     * @param universeSettings the universe settings
     */
    protected open fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(true)

    /**
     * Check if the universe has this command, and it can be executed on the player
     *
     * @param playerData the command execute on this player
     * @param universeSettings universe setting, e.g., have
     */
    fun canExecuteOnPlayer(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val hasCommand = CommandErrorMessage(
            CommandCollection.hasCommand(universeSettings, this),
            I18NString(
                listOf(
                    NormalString("No such command: "),
                    IntString(0),
                    NormalString(". ")
                ),
                listOf(
                    this.toString()
                ),
            )
        )

        val isToIdValid = CommandErrorMessage(
            checkToId(playerData),
            I18NString(
                listOf(
                    NormalString("Player id "),
                    IntString(0),
                    NormalString(" is not the same as the toId "),
                    IntString(1),
                    NormalString(" in this command. ")
                ),
                listOf(
                    playerData.playerId.toString(),
                    toId.toString(),
                ),
            )
        )

        val canExecute = canExecute(playerData, universeSettings)

        return CommandErrorMessage(
            listOf(
                hasCommand,
                isToIdValid,
                canExecute,
            )
        )
    }


    /**
     * Execute on playerData, for AI/human planning and action
     */
    protected abstract fun execute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    )


    /**
     * Check and execute
     */
    fun checkAndExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val executeMessage: CommandErrorMessage = canExecuteOnPlayer(playerData, universeSettings)

        if (executeMessage.success) {
            try {
                execute(playerData, universeSettings)
            } catch (e: Throwable) {
                logger.error("checkAndExecute fail, throwable $e")
                throw e
            }
        } else {
            val className = this::class.qualifiedName
            logger.debug("$className cannot be executed on $toId: ${executeMessage.errorMessage.toNormalString()}")
        }

        return executeMessage
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

fun Command.name(): String = this::class.simpleName.toString()

fun <T : Command> KClass<T>.name(): String = this.simpleName.toString()

sealed class CommandAvailability {
    // Command list allowed to be sent and executed
    abstract val commandList: List<String>

    // Event list allowed to be added by AddEventCommand
    abstract val addEventList: List<String>
}

fun CommandAvailability.name(): String = this::class.simpleName.toString()

object CommandCollection {
    private val logger = RelativitizationLogManager.getLogger()

    val commandAvailabilityNameMap: Map<String, CommandAvailability> = CommandAvailability::class
        .sealedSubclasses.map {
            it.objectInstance!!
        }.associateBy {
            it.name()
        }

    fun hasCommand(universeSettings: UniverseSettings, command: Command): Boolean {
        return if (universeSettings.commandCollectionName != AllCommandAvailability.name()) {
            if (commandAvailabilityNameMap.containsKey(universeSettings.commandCollectionName)) {
                commandAvailabilityNameMap.getValue(
                    universeSettings.commandCollectionName
                ).commandList.contains(command.name())
            } else {
                logger.error("No command collection name: ${universeSettings.commandCollectionName} found")
                false
            }
        } else {
            true
        }
    }
}

/**
 * Store the success state and error message if not success
 *
 * @property success is the command successfully sent / executed
 * @property errorMessage the error message if failed
 */
@Serializable
data class CommandErrorMessage(
    val success: Boolean,
    val errorMessage: I18NString = I18NString(listOf(), listOf())
) {
    constructor(success: Boolean, i18NStringList: List<I18NString>) : this(
        success,
        I18NString.combine(i18NStringList)
    )

    constructor(commandErrorMessageList: List<CommandErrorMessage>) : this(
        commandErrorMessageList.all { it.success },
        commandErrorMessageList.filter { !it.success }.map { it.errorMessage }
    )
}

object CommandI18NStringFactory {
    fun isNotTopLeader(playerId: Int): I18NString = I18NString(
        listOf(
            NormalString("Player "),
            IntString(0),
            NormalString(" is not a top leader. "),
        ),
        listOf(
            playerId.toString(),
        )
    )

    fun isTopLeader(playerId: Int): I18NString = I18NString(
        listOf(
            NormalString("Player "),
            IntString(0),
            NormalString(" is a top leader. "),
        ),
        listOf(
            playerId.toString(),
        )
    )

    fun isNotDirectLeader(playerId: Int, otherPlayerId: Int): I18NString = I18NString(
        listOf(
            NormalString("Player "),
            IntString(0),
            NormalString(" not a direct leader of player "),
            IntString(1),
            NormalString(". ")
        ),
        listOf(
            otherPlayerId.toString(),
            playerId.toString(),
        )
    )

    fun isNotDirectSubordinate(playerId: Int, otherPlayerId: Int): I18NString = I18NString(
        listOf(
            NormalString("Player "),
            IntString(0),
            NormalString(" not a direct subordinate of player "),
            IntString(1),
            NormalString(". ")
        ),
        listOf(
            otherPlayerId.toString(),
            playerId.toString(),
        )
    )

    fun isNotSubordinate(playerId: Int, otherPlayerId: Int): I18NString = I18NString(
        listOf(
            NormalString("Player "),
            IntString(0),
            NormalString(" not a subordinate of player "),
            IntString(1),
            NormalString(". ")
        ),
        listOf(
            otherPlayerId.toString(),
            playerId.toString(),
        )
    )

    fun isNotToSelf(playerId: Int, toId: Int): I18NString = I18NString(
        listOf(
            NormalString("Player id "),
            IntString(0),
            NormalString(" is not the same as toId "),
            IntString(1),
            NormalString(". ")
        ),
        listOf(
            playerId.toString(),
            toId.toString(),
        )
    )


    fun isNotFromSelf(playerId: Int, fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Player id "),
            IntString(0),
            NormalString(" is not the same as fromId "),
            IntString(1),
            NormalString(". ")
        ),
        listOf(
            playerId.toString(),
            fromId.toString(),
        )
    )

    fun isTopLeaderIdWrong(
        playerTopLeaderId: Int,
        commandTopLeaderId: Int
    ): I18NString = I18NString(
        listOf(
            NormalString("Command top leader id "),
            IntString(0),
            NormalString(" is not the same as player top leader id "),
            IntString(1),
            NormalString(". ")
        ),
        listOf(
            commandTopLeaderId.toString(),
            playerTopLeaderId.toString()
        )
    )
}