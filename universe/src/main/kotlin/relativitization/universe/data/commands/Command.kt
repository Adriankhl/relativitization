package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.NormalString
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.reflect.KClass

@Serializable
sealed class Command {
    abstract val toId: Int
    abstract val fromId: Int
    abstract val fromInt4D: Int4D

    /**
     * Description of the command
     */
    abstract fun description(): I18NString


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
     * Check if the player (sender) can send the command
     */
    protected abstract fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage

    /**
     * Check if can send and have command
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

        if (!hasCommand.success ||
            !isFromIdValid.success ||
            !isFromInt4DValid.success ||
            !(canSendErrorMessage.success)
        ) {
            val className = this::class.qualifiedName
            logger.error("${className}: cannot send command")
        }

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
        return if (canSendFromPlayer(playerData, universeSettings).success) {
            try {
                selfExecuteBeforeSend(playerData, universeSettings)
                CommandErrorMessage(true)
            } catch (e: Throwable) {
                logger.error("checkAndSelfExecuteBeforeSend fail, throwable $e")
                throw e
            }
        } else {
            val className = this::class.qualifiedName
            logger.info("$className cannot be sent by $fromId")
            val reasonI18NString = I18NString("Reason: ")
            CommandErrorMessage(
                false,
                listOf(
                    reasonI18NString,
                    canSendFromPlayer(playerData, universeSettings).errorMessage
                )
            )
        }
    }


    /**
     * Check if the player can receive the command
     */
    protected abstract fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage

    /**
     * Check if can execute and have command
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
            logger.info("$className cannot be executed on $toId")
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
    abstract val commandList: List<String>

    // Allowed event list for AddEventCommand
    abstract val addEventList: List<String>
}

fun CommandAvailability.name(): String = this::class.simpleName.toString()

object CommandCollection {
    private val logger = RelativitizationLogManager.getLogger()

    private val commandAvailabilityList: List<CommandAvailability> =
        CommandAvailability::class.sealedSubclasses.map {
            it.objectInstance!!
        }

    val commandAvailabilityNameMap: Map<String, CommandAvailability> = commandAvailabilityList.map {
        it.name() to it
    }.toMap()

    fun hasCommand(universeSettings: UniverseSettings, command: Command): Boolean {
        return if (universeSettings.commandCollectionName != "All") {
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

    constructor(commandErrorMessageList: List<CommandErrorMessage>): this(
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

    fun isNotDirectSubordinate(playerId: Int, toId: Int): I18NString = I18NString(
        listOf(
            NormalString("Player "),
            IntString(0),
            NormalString(" not a direct subordinate of player "),
            IntString(1),
            NormalString(". ")
        ),
        listOf(
            toId.toString(),
            playerId.toString(),
        )
    )

    fun isNotSubordinate(playerId: Int, toId: Int): I18NString = I18NString(
        listOf(
            NormalString("Player "),
            IntString(0),
            NormalString(" not a subordinate of player "),
            IntString(1),
            NormalString(". ")
        ),
        listOf(
            toId.toString(),
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

    fun isTopLeaderIdWrong(commandTopLeaderId: Int, playerTopLeaderId: Int): I18NString = I18NString(
        listOf(
            NormalString("Command top leader id "),
            IntString(0),
            NormalString("is not the same as player top leader id "),
            IntString(1),
            NormalString(". ")
        ),
        listOf(
            commandTopLeaderId.toString(),
            playerTopLeaderId.toString()
        )
    )
}