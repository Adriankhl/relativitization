package relativitization.universe.game.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.CommandErrorMessage
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.core.maths.physics.Int4D
import relativitization.universe.game.data.components.defaults.popsystem.CarrierData
import relativitization.universe.game.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.game.data.components.syncData

/**
 * Remove a player and merge its carrier to another player
 * Cannot be sent by player, through mechanism only
 *
 * @param carrierList the list of carrier to be merged
 */
@Serializable
data class MergeCarrierCommand(
    override val toId: Int,
    val carrierList: List<CarrierData>
) : DefaultCommand() {
    override fun name(): String = "Merge Carrier"
    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        return CommandErrorMessage(false)
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        val newCarrierList: List<MutableCarrierData> = DataSerializer.copy(carrierList)
        newCarrierList.forEach {
            playerData.playerInternalData.popSystemData().addCarrier(it)
        }
        playerData.syncData()
    }
}