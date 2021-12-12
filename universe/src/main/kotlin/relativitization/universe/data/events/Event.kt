package relativitization.universe.data.events

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.CommandErrorMessage
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.reflect.KClass

@Serializable
sealed class Event {

    // The event belongs (or will belong) to this player
    abstract val toId: Int

    // The event sent from this player id, -1 if this is an auto-event
    abstract val fromId: Int

    // how many turns will this event stay in the player data
    abstract val stayTime: Int

    // Description of the event
    abstract val description: I18NString

    // Available choice description
    abstract val choiceDescription: Map<Int, I18NString>

    /**
     * Whether the player can send this event to other player
     */
    abstract fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage


    /**
     * Whether this event can be added to the player
     */
    abstract fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean

    /**
     * Generate commands once per turn
     *
     * @param eventId the id of this event in eventMap
     * @param mutableEventRecordData the record data of this event
     * @param universeData3DAtPlayer the 3d data to determine the generated command
     */
    abstract fun generateCommands(
        eventId: Int,
        mutableEventRecordData: MutableEventRecordData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): List<Command>

    /**
     * Generate default choice if no choice is decided, i.e., choice = 0
     *
     * @param eventId the id of this event in eventMap
     * @param mutableEventRecordData the record data of this event
     * @param universeData3DAtPlayer the 3d data to determine the generated command
     */
    abstract fun defaultChoice(
        eventId: Int,
        mutableEventRecordData: MutableEventRecordData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Int

    /**
     * Whether this event should be cancel, such as the player made a choice or some condition has been met
     *
     * @param eventId the id of this event in eventMap
     * @param mutableEventRecordData the record data of this event
     * @param universeData3DAtPlayer the universe data view by the player having this event
     */
    abstract fun shouldCancelThisEvent(
        eventId: Int,
        mutableEventRecordData: MutableEventRecordData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Boolean

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

fun Event.name(): String = this::class.simpleName.toString()

fun <T : Event> KClass<T>.name(): String = this.simpleName.toString()

/**
 * Unit of event data
 *
 * @property event the event
 * @property eventRecordData the record of the event data, e.g., choice and counter
 */
@Serializable
data class EventData(
    val event: Event,
    val eventRecordData: EventRecordData = EventRecordData(),
)

@Serializable
data class MutableEventData(
    val event: Event,
    var eventRecordData: MutableEventRecordData = MutableEventRecordData(),
)

/**
 * Record of the event, including the choice and counter
 *
 * @property hasChoice the player has a choice (instead of the default choice)
 * @property choice choice of the player
 * @property stayCounter number of turns the event has been stayed in the player data
 */
@Serializable
data class EventRecordData(
    val hasChoice: Boolean = false,
    val choice: Int = 0,
    val stayCounter: Int = 0
)

@Serializable
data class MutableEventRecordData(
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