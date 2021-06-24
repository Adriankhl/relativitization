package relativitization.universe.data.events

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.RelativitizationLogManager

@Serializable
sealed class Event {

    // Name of the event
    abstract val name: EventName

    // The event belongs (or will belong) to this player
    abstract val toId: Int

    // The event sent from this player id, -1 if this is an auto-event
    abstract val fromId: Int

    // Description of the event
    abstract val description: String

    // Available choice description
    abstract val choiceDescription: Map<Int, String>

    // how many turns will this event stay in the player data
    abstract val stayTime: Int

    /**
     * Whether this event should be cancel, such as the player made a choice or some condition has been met
     *
     * @param mutableEventData the event data of this event
     * @param universeData3DAtPlayer the universe data view by the player having this event
     */
    abstract fun shouldCancelThisEvent(
        mutableEventData: MutableEventData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Boolean

    // Generate default choice if no choice is given to help ai decision
    abstract fun defaultChoice(universeData3DAtPlayer: UniverseData3DAtPlayer): Int

    // Whether the player can send this event to other player
    abstract fun canSend(playerData: PlayerData, universeSettings: UniverseSettings): Boolean

    // Whether this event can be added to the player
    abstract fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean

    // generate commands
    // call once per turn
    abstract fun generateCommands(choice: Int, universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command>

    companion object {
        private val logger = RelativitizationLogManager.getLogger()

        val defaultAddEventList: List<EventName> = EventName.values().toList()

        fun canAddEvent(universeSettings: UniverseSettings, event: Event): Boolean {
            return when (universeSettings.commandCollectionName) {
                "DefaultCommands" -> {
                    defaultAddEventList.contains(event.name)
                }
                else -> {
                    logger.error("No add event command collection name: ${universeSettings.commandCollectionName} found")
                    defaultAddEventList.contains(event.name)
                }
            }
        }
    }
}


/**
 * Names of event, aid event comparison and grouping
 */
enum class EventName(val value: String) {
    MOVE_TO_DOUBLE3D("Move to double3D")
    ;

    override fun toString(): String {
        return value
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
    val hasChoice: Boolean = false,
    val choice: Int = 0,
    val stayCounter: Int = 0
)

@Serializable
data class MutableEventData(
    val event: Event,
    var hasChoice: Boolean = false,
    var choice: Int = 0,
    var stayCounter: Int = 0
)

/**
 * Given the universe data 3D view, some events should be generated probabilistically if certain condition is satisfied
 */
interface AutoEvent {
    fun generateEventList(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Event>
}