package relativitization.universe.data.events

import kotlinx.serialization.Serializable
import org.apache.logging.log4j.LogManager
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.Command

@Serializable
sealed class Event {

    // Name of the event
    abstract val name: String

    // Description of the event
    abstract val description: String

    // Available choice description
    abstract val choiceDescription: Map<Int, String>

    // Default choice in availableCommandList
    // -1 when no choice
    abstract val default: Int

    // how many turns will this event stay in the player data
    abstract val stayTime: Int

    // generate commands
    abstract fun generateCommands(choice: Int): List<Command>

    // turn between commands generation
    // 0 when only generate once
    abstract val turnPerGenerate: Int

    companion object {
        private val logger = LogManager.getLogger()
    }
}

/**
 * Unit of event data
 *
 * @property event the event
 * @property choice choice of the player
 * @property stayCounter number of turns the event has been stayed in the player data
 */
@Serializable
data class EventData(
    val event: Event,
    val choice: Int = -1,
    val stayCounter: Int = 0
)

@Serializable
data class MutableEventData(
    val event: Event,
    var choice: Int = -1,
    var stayCounter: Int = 0
)