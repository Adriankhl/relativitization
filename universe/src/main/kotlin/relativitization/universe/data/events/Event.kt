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

@Serializable
sealed class Event {

    // The event belongs (or will belong) to this player
    abstract val toId: Int

    /**
     * Name of the event
     */
    open fun name(): String = ""

    /**
     * Description of the event
     *
     * @param fromId the event is sent from the player with this Id
     */
    open fun description(fromId: Int): I18NString = I18NString("")

    /**
     * Generate a map from indexes of available choices to a description
     *
     * @param fromId the event is sent from the player with this Id
     */
    open fun choiceDescription(fromId: Int): Map<Int, I18NString> = mapOf()

    /**
     * Whether the player can send this event to other player
     *
     * @param playerData the data of the player to be checked
     * @param universeSettings settings of the universe
     */
    open fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings,
    ): CommandErrorMessage = CommandErrorMessage(true)


    /**
     * Whether this event can be added to the player
     *
     * @param playerData the data of the player to be checked
     * @param fromId the event is sent from the player with this Id
     * @param universeSettings settings of the universe
     */
    open fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        universeSettings: UniverseSettings,
    ): CommandErrorMessage = CommandErrorMessage(true)

    /**
     * How many turns will this event stay in the player data
     *
     * @param fromId the event is sent from the player with this Id
     */
    open fun stayTime(fromId: Int): Int = 5

    /**
     * Whether this event should be cancelled, such as some conditions have been met
     *
     * @param mutablePlayerData the player data containing this event    *
     * @param fromId the event is sent from the player with this Id
     * @param universeData3DAtPlayer the universe data view by the player having this event
     * @param universeSettings the universe setting
     */
    abstract fun shouldCancel(
        mutablePlayerData: MutablePlayerData,
        fromId: Int,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
    ): Boolean


    /**
     * Action and generate commands depending on the choice
     *
     * @param mutablePlayerData the player data containing this event
     * @param fromId the event is sent from the player with this Id
     * @param universeData3DAtPlayer the 3d data to determine the generated command
     * @param universeSettings the universe setting
     */
    abstract fun choiceAction(
        mutablePlayerData: MutablePlayerData,
        fromId: Int,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
    ): Map<Int, () -> List<Command>>

    /**
     * Generate default choice if no choice is decided, i.e., choice = 0
     *
     * @param mutablePlayerData the player data containing this event
     * @param fromId the event is sent from the player with this Id
     * @param universeData3DAtPlayer the 3d data to determine the generated command
     * @param universeSettings the universe setting
     * @param random random number generator
     */
    abstract fun defaultChoice(
        mutablePlayerData: MutablePlayerData,
        fromId: Int,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        random: Random,
    ): Int

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Unit of event data
 *
 * @property event the event
 * @param fromId the event is sent from the player with this Id
 * @property eventRecordData the record of the event data, e.g., choice and counter
 */
@Serializable
data class EventData(
    val event: Event,
    val fromId: Int,
    val eventRecordData: EventRecordData = EventRecordData(),
)

@Serializable
data class MutableEventData(
    val event: Event,
    val fromId: Int,
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