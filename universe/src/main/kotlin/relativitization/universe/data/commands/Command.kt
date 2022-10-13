package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.events.Event
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

    /**
     * Name of the command
     */
    open fun name(): String = ""

    /**
     * Description of the command, default to empty description
     *
     * @param fromId the command is sent from the player of this Id
     */
    open fun description(fromId: Int): I18NString = I18NString("")

    /**
     * Check to see if toId match
     *
     * @param playerData check this player data
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
     * @param universeSettings settings of the universe
     */
    protected open fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings,
    ): CommandErrorMessage = CommandErrorMessage(true)

    /**
     * Check if the universe has this command, and it can be sent by the player
     *
     * @param playerData the player data to send this command
     * @param universeSettings settings of the universe
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

        val canSendErrorMessage: CommandErrorMessage = canSend(
            playerData = playerData,
            universeSettings = universeSettings
        )

        return CommandErrorMessage(
            listOf(
                hasCommand,
                canSendErrorMessage,
            )
        )
    }

    /**
     * Execute on self in order to end this command
     *
     * @param playerData self-execute on the player
     * @param universeSettings settings of the universe
     */
    protected open fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings,
    ) { }

    /**
     * Check and self execute
     *
     * @param playerData self-execute on the player
     * @param universeSettings settings of the universe
     */
    fun checkAndSelfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings,
    ): CommandErrorMessage {
        val sendMessage: CommandErrorMessage = canSendFromPlayer(
            playerData = playerData,
            universeSettings = universeSettings
        )

        if (sendMessage.success) {
            try {
                selfExecuteBeforeSend(
                    playerData = playerData,
                    universeSettings = universeSettings
                )
            } catch (e: Throwable) {
                logger.error("checkAndSelfExecuteBeforeSend fail, throwable $e")
                throw e
            }
        } else {
            val className = this::class.qualifiedName
            logger.debug("$className cannot be sent by ${playerData.playerId}:" +
                    " ${sendMessage.errorMessage.toNormalString()}")
        }

        return sendMessage
    }


    /**
     * Check if the player can receive the command, default to always true
     *
     * @param playerData the data of the player to execute this command
     * @param fromId the command is sent from the player of this Id
     * @param fromInt4D the command is sent from this location
     * @param universeSettings the universe settings
     */
    protected open fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(true)

    /**
     * Check if the universe has this command, and it can be executed on the player
     *
     * @param playerData the command execute on this player
     * @param fromId the command is sent from the player of this Id
     * @param fromInt4D the command is sent from this location
     * @param universeSettings settings of the universe
     */
    fun canExecuteOnPlayer(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
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

        val canExecute: CommandErrorMessage = canExecute(
            playerData = playerData,
            fromId = fromId,
            fromInt4D = fromInt4D,
            universeSettings = universeSettings
        )

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
     *
     * @param playerData the command execute on this player
     * @param fromId the command is sent from the player of this Id
     * @param fromInt4D the command is sent from this location
     * @param universeSettings settings of the universe
     */
    protected abstract fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    )


    /**
     * Check and execute
     *
     * @param playerData the command execute on this player
     * @param fromId the command is sent from the player of this Id
     * @param fromInt4D the command is sent from this location
     * @param universeSettings settings of the universe
     */
    fun checkAndExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val executeMessage: CommandErrorMessage = canExecuteOnPlayer(
            playerData = playerData,
            fromId = fromId,
            fromInt4D = fromInt4D,
            universeSettings = universeSettings
        )

        if (executeMessage.success) {
            try {
                execute(
                    playerData = playerData,
                    fromId = fromId,
                    fromInt4D = fromInt4D,
                    universeSettings = universeSettings
                )
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

/**
 * Holding a command and the associated data
 *
 * @property command the command
 * @property fromId the command is sent from the player of this Id
 * @property fromInt4D the command is sent from this location
 */
@Serializable
data class CommandData(
    val command: Command,
    val fromId: Int,
    val fromInt4D: Int4D,
)

sealed class CommandAvailability {
    // Command list allowed to be sent and executed
    abstract val commandList: List<KClass<out Command>>

    // Event list allowed to be added by AddEventCommand
    abstract val addEventList: List<KClass<out Event>>

    private val commandNameSet: Set<String> by lazy {
        commandList.map {
            it.simpleName.toString()
        }.toSet()
    }

    private val addEventNameSet: Set<String> by lazy {
        addEventList.map {
            it.simpleName.toString()
        }.toSet()
    }

    abstract fun name(): String

    fun hasCommand(command: Command): Boolean {
        return commandNameSet.contains(command::class.simpleName)
    }

    fun canAddEvent(event: Event): Boolean {
        return addEventNameSet.contains(event::class.simpleName)
    }
}

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
                ).hasCommand(command)
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