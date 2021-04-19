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
     * Execute on playerData, for AI/human planning and action
     *
     * @return pair of list of command and list of data of new player to be created
     */
    abstract fun execute(
        playerData: PlayerData,
    ): List<Command>

    /**
     * Check id and and execute, for delayed execution of the command to player
     */
    fun checkAndExecute(
        playerData: PlayerData,
    ): Pair<Boolean, List<Command>> {
        return if (playerData.id != toId) {
            val className = this.javaClass.kotlin.qualifiedName
            logger.error("${className}: player id not equal to command target id")
            Pair(false, listOf())
        } else {
            val generateCommand = execute(playerData)
            Pair(true, generateCommand)
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}