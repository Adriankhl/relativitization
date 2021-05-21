package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.physics.Int4D
import org.apache.logging.log4j.LogManager
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseSettings
import kotlin.reflect.KClass

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
    abstract fun canSend(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean

    /**
     * Check if can send and have command
     */
    fun canSendAndHaveCommand(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return Command.haveCommand(universeSettings, this) && canSend(playerData, universeSettings)
    }


    /**
     * Check if the player can receive the command
     */
    abstract fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean

    /**
     * Check if can execute and have command
     */
    fun canExecuteAndHaveCommand(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return Command.haveCommand(universeSettings, this) && canExecute(playerData, universeSettings)
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
        return if (checkId(playerData) && canExecuteAndHaveCommand(playerData, universeSettings)) {
            execute(playerData, universeSettings)
        } else {
            val className = this::class.qualifiedName
            logger.info("$className cannot be executed on $toId")
        }
    }

    companion object {
        private val logger = LogManager.getLogger()

        val defaultCommandList: List<String> = listOf(
            "ChangeVelocity"
        )

        val commandCollectionList: List<String> = listOf(
            "DefaultCommand"
        )

        fun haveCommand(universeSettings: UniverseSettings, command: Command): Boolean {
            return when (universeSettings.commandCollectionName) {
                "DefaultCommand" -> {
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