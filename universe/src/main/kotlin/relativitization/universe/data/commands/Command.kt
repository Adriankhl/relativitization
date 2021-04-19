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

    abstract val senderRequirement: SenderRequirement

    /**
     * Execute on playerData, for AI/human planning and action
     *
     * @return pair of list of command and list of data of new player to be created
     */
    abstract fun execute(
        playerData: PlayerData,
    ): Unit

    /**
     * Check id and and execute, for delayed execution of the command to player
     */
    fun checkAndExecute(
        playerData: PlayerData,
    ): Boolean {
        return if (playerData.id != toId) {
            val className = this.javaClass.kotlin.qualifiedName
            logger.error("${className}: player id not equal to command target id")
            false
        } else {
            execute(playerData)
            true
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}

enum class SenderRequirement {
    LEADER,
    CASUAL
}