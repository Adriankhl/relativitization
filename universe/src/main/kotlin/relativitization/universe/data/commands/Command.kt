package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.data.events.MoveToDouble3DEvent
import relativitization.universe.data.events.name
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.RealString
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
    abstract val description: I18NString


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
    protected abstract fun canSend(playerData: MutablePlayerData, universeSettings: UniverseSettings): CanSendWithMessage

    /**
     * Check if can send and have command
     *
     * @param playerData the player data to send this command
     */
    fun canSendFromPlayer(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        val hasCommand: Boolean = CommandCollection.hasCommand(universeSettings, this)
        val canSend: Boolean =  canSend(playerData, universeSettings).canSend
        val isPlayerDataValid: Boolean = (playerData.int4D.toInt4D() == fromInt4D) &&
                (checkFromId(playerData))
        if (!hasCommand || !canSend || !isPlayerDataValid) {
            val className = this::class.qualifiedName
            logger.error("${className}: cannot send command, hasCommand: $hasCommand, canSend: $canSend, isPlayerDataValid: $isPlayerDataValid")
        }

        return hasCommand && canSend && isPlayerDataValid
    }


    /**
     * Check if the player can receive the command
     */
    protected abstract fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean

    /**
     * Check if can execute and have command
     *
     * @param playerData the command execute on this player
     * @param universeSettings universe setting, e.g., have
     */
    fun canExecuteOnPlayer(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        val hasCommand: Boolean = CommandCollection.hasCommand(universeSettings, this)
        val correctId: Boolean = checkToId(playerData)
        val canExecute: Boolean = canExecute(playerData, universeSettings)
        return hasCommand && correctId && canExecute
    }

    /**
     * Execute on self in order to end this command
     */
    protected open fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {}

    /**
     * Check and self execute
     */
    fun checkAndSelfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return if (canSendFromPlayer(playerData, universeSettings)) {
            try {
                selfExecuteBeforeSend(playerData, universeSettings)
                true
            } catch (e: Throwable) {
                logger.error("checkAndSelfExecuteBeforeSend fail, throwable $e")
                throw e
            }
        } else {
            val className = this::class.qualifiedName
            logger.info("$className cannot be sent by $fromId")
            false
        }
    }

    /**
     * Execute on playerData, for AI/human planning and action
     */
    protected abstract fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings)


    /**
     * Check and execute
     */
    fun checkAndExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return if (canExecuteOnPlayer(playerData, universeSettings)) {
            try {
                execute(playerData, universeSettings)
                true
            } catch (e: Throwable) {
                logger.error("checkAndExecute fail, throwable $e")
                throw e
            }
        } else {
            val className = this::class.qualifiedName
            logger.info("$className cannot be executed on $toId")
            false
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

fun Command.name(): String = this::class.simpleName.toString()

fun <T : Command> KClass<T>.name(): String = this.simpleName.toString()

abstract class CommandList {
    abstract val commandList: List<String>

    // Allowed event list for AddEventCommand
    abstract val eventList: List<String>
}

object DefaultCommandList : CommandList() {
    override val commandList: List<String> = listOf(
        AddEventCommand::class.name(),
        ChangeVelocityCommand::class.name(),
        CannotSendCommand::class.name(),
        DisableFuelIncreaseCommand::class.name(),
        DummyCommand::class.name(),
        SelectEventChoiceCommand::class.name(),
    )

    override val eventList: List<String> = listOf(
        MoveToDouble3DEvent::class.name(),
    )
}

fun CommandList.name(): String = this::class.simpleName.toString()

object CommandCollection {
    private val logger = RelativitizationLogManager.getLogger()

    private val commandListList: List<CommandList> = listOf(
        DefaultCommandList
    )

    val commandListNameMap: Map<String, CommandList> = commandListList.map {
        it.name() to it
    }.toMap()

    fun hasCommand(universeSettings: UniverseSettings, command: Command): Boolean {
        val commandCollection: List<String> = commandListNameMap.getOrElse(
            universeSettings.commandCollectionName
        ) {
            logger.error("No command collection name: ${universeSettings.commandCollectionName} found")
            DefaultCommandList
        }.commandList

        return commandCollection.contains(command.name())
    }
}

@Serializable
data class CanSendWithMessage(
    val canSend: Boolean,
    val message: I18NString = I18NString(listOf(), listOf())
)

object CanSendWIthMessageI18NStringFactory {
    fun isNotSubordinate(playerId: Int, toId: Int): I18NString = I18NString(
        listOf(
            RealString("Player "),
            IntString(0),
            RealString(" not a subordinate of "),
            IntString(1),
            RealString(".")
        ),
        listOf(
            toId.toString(),
            playerId.toString(),
        )
    )

    fun isToIdWrong(playerId: Int, toId: Int): I18NString = I18NString(
        listOf(
            RealString("Player id"),
            IntString(0),
            RealString(" the same as toId "),
            IntString(1),
            RealString(".")
        ),
        listOf(
            playerId.toString(),
            toId.toString(),
        )
    )

    fun isTopLeaderIdWrong(playerTopLeaderId: Int, topLeaderId: Int): I18NString = I18NString(
        listOf(
            RealString("Player top leader id "),
            IntString(0),
            RealString("the same as "),
            IntString(1),
            RealString(".")
        ),
        listOf(
            playerTopLeaderId.toString(),
            topLeaderId.toString()
        )
    )
}