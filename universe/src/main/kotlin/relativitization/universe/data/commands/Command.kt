package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.physics.Int4D
import org.apache.logging.log4j.LogManager
import relativitization.universe.data.PlayerData

@Serializable
sealed class Command {
    abstract val toId: Int
    abstract val fromId: Int
    abstract val fromInt4D: Int4D

    /**
     * Check if the player (sender) can send the command
     * Should be overwritten by subclass if required
     */
    fun canSend(playerData: PlayerData): Boolean {
        return true
    }

    /**
     * Check if the player can receive the command
     * Should be overwritten by subclass if required
     */
    fun canExecute(playerData: PlayerData): Boolean {
        return true
    }


    /**
     * Check to see if id match
     */
    fun checkId(playerData: PlayerData): Boolean {
        return if (playerData.id == toId) {
            true
        } else {
            val className = this.javaClass.kotlin.qualifiedName
            logger.error("${className}: player id not equal to command target id")
            false
        }
    }

    /**
     * Execute on playerData, for AI/human planning and action
     */
    abstract fun execute(playerData: PlayerData): Unit


    /**
     * Check and execute
     */
    fun checkAndExecute(playerData: PlayerData): Unit {
        if (checkId(playerData) && canExecute(playerData)) {
            execute(playerData)
        } else {
            val className = this.javaClass.kotlin.qualifiedName
            logger.info("$className cannot be executed on $toId")
        }

    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}