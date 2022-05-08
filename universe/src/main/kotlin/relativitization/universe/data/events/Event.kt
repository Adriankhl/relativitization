package relativitization.universe.data.events

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.CommandErrorMessage
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.random.Random
import kotlin.reflect.KClass

@Serializable
sealed class Event {

    // The event belongs (or will belong) to this player
    abstract val toId: Int

    // The event sent from this player id, -1 if this is an auto-event
    abstract val fromId: Int

    // Description of the event
    open fun description(): I18NString = I18NString("")

    // Available choice description
    open fun choiceDescription(): Map<Int, I18NString> = mapOf()

    /**
     * Whether the player can send this event to other player
     */
    open fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(true)


    /**
     * Whether this event can be added to the player
     */
    open fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(true)

    /**
     * How many turns will this event stay in the player data
     */
    open fun stayTime(): Int = 5

    /**
     * Whether this event should be cancelled, such as some conditions have been met
     *
     * @param mutablePlayerData the player data containing this event    *
     * @param universeData3DAtPlayer the universe data view by the player having this event
     * @param universeSettings the universe setting
     */
    abstract fun shouldCancel(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
    ): Boolean


    /**
     * Action and generate commands depending on the choice
     *
     * @param mutablePlayerData the player data containing this event
     * @param universeData3DAtPlayer the 3d data to determine the generated command
     * @param universeSettings the universe setting
     */
    abstract fun choiceAction(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
    ): Map<Int, () -> List<Command>>

    /**
     * Generate default choice if no choice is decided, i.e., choice = 0
     *
     * @param mutablePlayerData the player data containing this event
     * @param universeData3DAtPlayer the 3d data to determine the generated command
     * @param universeSettings the universe setting
     * @param random random number generator
     */
    abstract fun defaultChoice(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        random: Random,
    ): Int

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