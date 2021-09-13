package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.data.events.MoveToDouble3DEvent
import relativitization.universe.data.events.name
import relativitization.universe.utils.I18NString
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
     * Check if the player (sender) can send the command
     */
    protected abstract fun canSend(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean

    /**
     * Check if can send and have command
     *
     * @param playerData the player data to send this command
     */
    fun canSendFromPlayer(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        val hasCommand: Boolean = CommandCollection.hasCommand(universeSettings, this)
        val canSend: Boolean =  canSend(playerData, universeSettings)
        val isPlayerDataValid: Boolean = (playerData.int4D.toInt4D() == fromInt4D) &&
                (playerData.playerId == fromId)
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
        val canExecute: Boolean = canExecute(playerData, universeSettings)
        return hasCommand && canExecute
    }

    /**
     * Check to see if id match
     */
    private fun checkId(playerData: MutablePlayerData): Boolean {
        return if (playerData.playerId == toId) {
            true
        } else {
            val className = this::class.qualifiedName
            logger.error("${className}: player id not equal to command target id")
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
    fun checkAndExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        return if (checkId(playerData) && canExecuteOnPlayer(playerData, universeSettings)) {
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