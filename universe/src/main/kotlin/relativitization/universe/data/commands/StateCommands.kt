package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.physics.Int4D
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.data.state.temporary.DisableFuelProductionState

@Serializable
data class DisableFuelProductionCommand(
    val disableFuelProductionState: DisableFuelProductionState,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    override val toId: Int,
) : Command() {

    override val name: String = "Disable Fuel Production"

    override fun description(): String {
        return "Disable fuel production for ${disableFuelProductionState.timeRemain} turn"
    }

    override fun canSend(playerData: PlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.id == toId
    }

    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.id == fromId
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.playerState.temporaryState.disableFuelProductionStateList.add(
            DataSerializer.copy(disableFuelProductionState)
        )
    }
}