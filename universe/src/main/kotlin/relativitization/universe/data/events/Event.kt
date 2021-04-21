package relativitization.universe.data.events

import kotlinx.serialization.Serializable
import org.apache.logging.log4j.LogManager
import relativitization.universe.data.commands.Command

@Serializable
sealed class Event {
    val availableCommandList: List<Command> = listOf()
    val default: Int = 0

    companion object {
        private val logger = LogManager.getLogger()
    }
}