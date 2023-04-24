package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.defaults.popsystem.CarrierData
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.data.components.syncData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.maths.physics.Int4D

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