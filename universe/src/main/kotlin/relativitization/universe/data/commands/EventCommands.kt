package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.data.events.*
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.RealString
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

    override val description: I18NString = I18NString(
        listOf(
            RealString("Add event ("),
            IntString(0),
            RealString(") to player "),
            IntString(1),
            RealString(" from player "),
            IntString(2),
            RealString(". Event description: ")
        ),
        listOf(
            event.name(),
            toId.toString(),
            fromId.toString(),
        ),
    ) + event.description

    /**
     * Whether this player can send the event depends on the event
     */
    override fun canSend(playerData: PlayerData, universeSettings: UniverseSettings): Boolean {
        return validEventPlayerId() &&
                canAddEvent(universeSettings, event) &&
                event.canSend(playerData, universeSettings)
    }

    /**
     * Whether the event can be added to the player depends on the event
     */
    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return canAddEvent(universeSettings, event) &&
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

    companion object {
        private val logger = RelativitizationLogManager.getLogger()

        /**
         * Whether player can add this event to other player, used by AddEventCommand
         */
        fun canAddEvent(universeSettings: UniverseSettings, event: Event): Boolean {
            val addEventList: List<String> = CommandCollection.commandListNameMap.getOrElse(
                universeSettings.commandCollectionName
            ) {
                logger.error("No add event command collection name: ${universeSettings.commandCollectionName} found")
                DefaultCommandList
            }.eventList

            return addEventList.contains(event.name())
        }
    }
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
    val eventName: String,
    val choice: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    override val toId: Int,
) : Command() {

    override val description: I18NString = I18NString(
        listOf(
            RealString("Select choice "),
            IntString(0),
            RealString(" for event "),
            IntString(1),
            RealString(" ("),
            IntString(2),
            RealString(")")
        ),
        listOf(
            choice.toString(),
            eventKey.toString(),
            eventName.toString(),
        ),
    )

    override fun canSend(playerData: PlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.playerId == toId
    }

    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.playerId == fromId
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val eventDataMap: MutableMap<Int, MutableEventData> = playerData.playerInternalData.eventDataMap

        // Check if eventIndex is in range
        if (eventDataMap.containsKey(eventKey)) {
            val eventData: MutableEventData = eventDataMap.getValue(eventKey)
            if (eventData.event.name() == eventName) {
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