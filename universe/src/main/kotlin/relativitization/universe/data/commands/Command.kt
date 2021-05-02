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

    /**
     * Description of the command
     */
    abstract fun description(): String

    /**
     * Check if the player (sender) can send the command
     */
    abstract fun canSend(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean

    /**
     * Check if the player can receive the command
     */
    abstract fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean

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
        return if (checkId(playerData) && canExecute(playerData, universeSettings)) {
            execute(playerData, universeSettings)
        } else {
            val className = this::class.qualifiedName
            logger.info("$className cannot be executed on $toId")
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}