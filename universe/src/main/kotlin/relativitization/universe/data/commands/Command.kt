package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.utils.RelativitizationLogManager

@Serializable
sealed class Command {
    abstract val toId: Int
    abstract val fromId: Int
    abstract val fromInt4D: Int4D

    // name of this command
    // for haveCommand() function
    abstract val name: CommandName

    /**
     * Description of the command
     */
    abstract val description: String

    /**
     * Check if the player (sender) can send the command
     */
    protected abstract fun canSend(playerData: PlayerData, universeSettings: UniverseSettings): Boolean

    /**
     * Check if can send and have command
     *
     * @param playerData the player data to send this command
     */
    fun canSendFromPlayer(playerData: PlayerData, universeSettings: UniverseSettings): Boolean {
        val hasCommand: Boolean = CommandCollection.hasCommand(universeSettings, this)
        val canSend: Boolean =  canSend(playerData, universeSettings)
        val isPlayerDataValid: Boolean = (playerData.int4D == fromInt4D) && (playerData.playerId == fromId)
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

object CommandCollection {
    private val logger = RelativitizationLogManager.getLogger()

    val defaultCommandList: List<CommandName> = CommandName.values().toList()

    val commandListNameMap: Map<String, List<CommandName>> = mapOf(
        "DefaultCommands" to defaultCommandList
    )

    fun hasCommand(universeSettings: UniverseSettings, command: Command): Boolean {
        val commandCollection: List<CommandName> = commandListNameMap.getOrElse(
            universeSettings.commandCollectionName
        ) {
            logger.error("No command collection name: ${universeSettings.commandCollectionName} found")
            defaultCommandList
        }

        return commandCollection.contains(command.name)
    }
}

/**
 * Names of command, aid command comparison and grouping
 */
enum class CommandName(val value: String) {
    ADD_EVENT("Add event"),
    CHANGE_VELOCITY("Change velocity"),
    CANNOT_SEND("Cannot send"),
    DISABLE_FUEL_INCREASE("Disable fuel production"),
    DUMMY("Dummy"),
    SELECT_EVENT_CHOICE("Select event choice")
    ;

    override fun toString(): String {
        return value
    }
}