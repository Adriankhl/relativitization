package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.events.Event
import relativitization.universe.data.events.EventCollection
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
                EventCollection.canAddEvent(universeSettings, event) &&
                event.canSend(playerData, universeSettings)
    }

    /**
     * Whether the event can be added to the player depends on the event
     */
    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return EventCollection.canAddEvent(universeSettings, event) &&
                event.canExecute(playerData, universeSettings)
    }


    /**
     * Add event data to player data
     */
    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val eventData: MutableEventData = MutableEventData(event)
        playerData.playerInternalData.addEventData(eventData)
    }

    /**
     * Check whether the fromId and toId in the event is equal to those in the command
     */
    private fun validEventPlayerId(): Boolean = (fromId == event.fromId) && (toId == event.toId)
}

/**
 * Select event choice, can only apply to player himself
 *
 * @property eventKey the index of the event in eventDataList
 * @property eventName name of the event, for ensuring the command acts on the correct event
 * @property choice the player choice on the event
 */
@Serializable
data class SelectEventChoiceCommand(
    val eventKey: Int,
    val eventName: EventName,
    val choice: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    override val toId: Int,
) : Command() {
    override val name: CommandName = CommandName.SELECT_EVENT_CHOICE

    override val description: String = "Select choice $choice for event $eventKey ($eventName)"

    override fun canSend(playerData: PlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.id == toId
    }

    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.id == fromId
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val eventDataMap: MutableMap<Int, MutableEventData> = playerData.playerInternalData.eventDataMap

        // Check if eventIndex is in range
        if (eventDataMap.containsKey(eventKey)) {
            val eventData: MutableEventData = eventDataMap.getValue(eventKey)
            if (eventData.event.name == eventName) {
                eventData.hasChoice = true
                eventData.choice = choice
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