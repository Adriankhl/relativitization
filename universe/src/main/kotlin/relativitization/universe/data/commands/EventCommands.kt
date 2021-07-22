package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.events.Event
import relativitization.universe.data.events.EventName
import relativitization.universe.data.events.MutableEventData
import relativitization.universe.data.physics.Int4D
import relativitization.universe.utils.RelativitizationLogManager

/**
 * Add event to player
 */
@Serializable
data class AddEventCommand(
    val event: Event,
    override val fromInt4D: Int4D,
) : Command() {


    override val toId: Int = event.toId
    override val fromId: Int = event.fromId

    override val name: CommandName = CommandName.ADD_EVENT

    override val description: String = "Add event (${event.name}) to player $toId from player $fromId."

    /**
     * Whether this player can send the event depends on the event
     */
    override fun canSend(playerData: PlayerData, universeSettings: UniverseSettings): Boolean {
        return validEventPlayerId() &&
                Event.canAddEvent(universeSettings, event) &&
                event.canSend(playerData, universeSettings)
    }

    /**
     * Whether the event can be added to the player depends on the event
     */
    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return Event.canAddEvent(universeSettings, event) &&
                event.canExecute(playerData, universeSettings)
    }


    /**
     * Add event data to player data
     */
    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val eventData: MutableEventData = MutableEventData(event)
        playerData.playerInternalData.eventDataList.add(eventData)
    }

    /**
     * Check whether the fromId and toId in the event is equal to those in the command
     */
    private fun validEventPlayerId(): Boolean = (fromId == event.fromId) && (toId == event.toId)
}

/**
 * Select event choice, can only apply to player himself
 *
 * @property eventIndex the index of the event in eventDataList
 * @property eventName name of the event, for ensuring the command acts on the correct event
 * @property choice the player choice on the event
 */
@Serializable
data class SelectEventChoiceCommand(
    val eventIndex: Int,
    val eventName: EventName,
    val choice: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    override val toId: Int,
) : Command() {
    override val name: CommandName = CommandName.SELECT_EVENT_CHOICE

    override val description: String = "Select choice $choice for event $eventIndex ($eventName)"

    override fun canSend(playerData: PlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.id == toId
    }

    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.id == fromId
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val eventDataList: MutableList<MutableEventData> = playerData.playerInternalData.eventDataList

        // Check if eventIndex is in range
        if (eventIndex < eventDataList.size ) {
            if (eventDataList[eventIndex].event.name == eventName) {
                eventDataList[eventIndex].hasChoice = true
                eventDataList[eventIndex].choice = choice
            } else {
                logger.error("Can't select event choice, wrong event name")
            }
        } else {
            logger.error("Can't select event choice, index out of range")
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }

}