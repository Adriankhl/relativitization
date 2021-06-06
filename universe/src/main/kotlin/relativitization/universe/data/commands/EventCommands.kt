package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.events.Event
import relativitization.universe.data.physics.Int4D

@Serializable
data class AddEventCommand(
    override val fromId: Int,
    override val toId: Int,
    override val fromInt4D: Int4D,
    val event: Event
) : Command() {
    override val name: String = "Add ${event.name}"

    override fun description(): String {
        return "Add event ${event.name}to player $toId from $fromId."
    }

    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return true
    }

    override fun canSend(playerData: PlayerData, universeSettings: UniverseSettings): Boolean {
       return true
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        TODO("Not yet implemented")
    }
}