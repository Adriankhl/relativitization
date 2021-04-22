package relativitization.universe.data.events

import com.github.javafaker.Bool
import kotlinx.serialization.Serializable
import org.apache.logging.log4j.LogManager
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command

@Serializable
sealed class Event {

    // Name of the event
    abstract val name: String

    // Description of the event
    abstract val description: String

    // Available choice description
    abstract val choiceDescription: Map<Int, String>

    // Generate default choice if no choice is given to help ai decision
    abstract fun defaultChoice(universeData3DAtPlayer: UniverseData3DAtPlayer): Int

    // how many turns will this event stay in the player data
    abstract val stayTime: Int

    // generate commands
    // call once per turn
    abstract fun generateCommands(choice: Int, universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command>

    companion object {
        private val logger = LogManager.getLogger()
    }
}

/**
 * Unit of event data
 *
 * @property event the event
 * @property hasChoice the player has a choice (instead of the default choice)
 * @property choice choice of the player
 * @property stayCounter number of turns the event has been stayed in the player data
 */
@Serializable
data class EventData(
    val event: Event,
    val hasChoice: Boolean,
    val choice: Int = 0,
    val stayCounter: Int = 0
)

@Serializable
data class MutableEventData(
    val event: Event,
    var hasChoice: Boolean,
    var choice: Int = -1,
    var stayCounter: Int = 0
)

/**
 * Given the universe data 3D view, some events should be generated probabilistically if certain condition is satisfied
 */
abstract class AutoEvent {
    abstract fun generateEventList(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Event>
}

/**
 * Object to store all the generator of events
 */
object AllAutoEvent {
    val autoEventList: List<AutoEvent> = listOf(
    )
}