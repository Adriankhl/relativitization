package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.physics.Int4D

@Serializable
data class DisableFuelProductionCommand(
    val disableFuelProductionTimeLimit: Int,
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
) : Command() {

    override val name: CommandName = CommandName.DISABLE_FUEL_PRODUCTION

    override fun description(): String {
        return "Disable fuel production for $disableFuelProductionTimeLimit turn"
    }

    override fun canSend(playerData: PlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.id == toId
    }

    override fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        return playerData.id == fromId
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.modifierData.physicsModifierData.disableFuelProductionByTime(
            disableFuelProductionTimeLimit
        )
    }
}