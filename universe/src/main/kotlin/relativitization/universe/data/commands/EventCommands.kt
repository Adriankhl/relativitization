package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.events.Event
import relativitization.universe.data.events.MutableEventData
import relativitization.universe.data.physics.Int4D

@Serializable
data class AddEventCommand(
    val event: Event,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    override val toId: Int = event.playerId,
) : Command() {
    override val name: String = "Add Event"

    override fun description(): String {
        return "Add event ${event.name}to player $toId from $fromId."
    }

    /**
     * Whether this player can send the event depends on the event
     */
    override fun canSend(playerData: PlayerData, universeSettings: UniverseSettings): Boolean {
        return event.canSend(playerData, toId, universeSettings)
    }

    /**
     * Whether the event can be added to the player depends on the event
     */
    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return event.canExecute(playerData, fromId, universeSettings)
    }


    /**
     * Add event data to player data
     */
    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val eventData: MutableEventData = MutableEventData(event)
        playerData.playerInternalData.eventDataList.add(eventData)
    }
}