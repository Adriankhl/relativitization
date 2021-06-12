package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.physics.Int4D
import org.apache.logging.log4j.LogManager
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseSettings

@Serializable
sealed class Command {
    abstract val toId: Int
    abstract val fromId: Int
    abstract val fromInt4D: Int4D

    // name of this command
    // for haveCommand() function
    abstract val name: String

    /**
     * Description of the command
     */
    abstract fun description(): String

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
        val hasCommand: Boolean = hasCommand(universeSettings, this)
        val canSend: Boolean =  canSend(playerData, universeSettings)
        val isPlayerDataValid: Boolean = (playerData.int4D == fromInt4D) && (playerData.id == fromId)
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
        val hasCommand: Boolean = hasCommand(universeSettings, this)
        val canExecute: Boolean = canExecute(playerData, universeSettings)
        return hasCommand && canExecute
    }

    /**
     * Check to see if id match
     */
    private fun checkId(playerData: MutablePlayerData): Boolean {
        return if (playerData.id == toId) {
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
        private val logger = LogManager.getLogger()

        val defaultCommandList: List<String> = listOf(
            "Add Event",
            "Change Velocity",
            "Disable Fuel Production",
            "Select Event Choice"
        )

        val commandCollectionList: List<String> = listOf(
            "DefaultCommands"
        )

        fun hasCommand(universeSettings: UniverseSettings, command: Command): Boolean {
            return when (universeSettings.commandCollectionName) {
                "DefaultCommands" -> {
                    defaultCommandList.contains(command.name)
                }
                else -> {
                    logger.error("No command collection name: ${universeSettings.commandCollectionName} found")
                    defaultCommandList.contains(command.name)
                }
            }
        }
    }
}