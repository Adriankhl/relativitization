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
     * Check if the player (sender) can send the command
     */
    abstract fun canSend(playerData: MutablePlayerData, universeSetting: UniverseSettings): Boolean

    /**
     * Check if the player can receive the command
     */
    abstract fun canExecute(playerData: MutablePlayerData, universeSetting: UniverseSettings): Boolean

    /**
     * Check to see if id match
     */
    fun checkId(playerData: MutablePlayerData): Boolean {
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
    abstract fun execute(playerData: MutablePlayerData, universeSetting: UniverseSettings): Unit


    /**
     * Check and execute
     */
    fun checkAndExecute(playerData: MutablePlayerData, universeSetting: UniverseSettings): Unit {
        return if (checkId(playerData) && canExecute(playerData, universeSetting)) {
            execute(playerData, universeSetting)
        } else {
            val className = this::class.qualifiedName
            logger.info("$className cannot be executed on $toId")
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}