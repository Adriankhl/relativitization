package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.default.physics.Int4D
import relativitization.universe.data.components.default.popsystem.CarrierData
import relativitization.universe.data.components.default.popsystem.MutableCarrierData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.I18NString

/**
 * Remove a player and merge its carrier to another player
 * Cannot be sent by player, through mechanism only
 *
 * @param carrierList the list of carrier to be merged
 */
@Serializable
data class MergeCarrierCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val carrierList: List<CarrierData>
) : DefaultCommand() {
    override val description: I18NString = I18NString("")

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        return CanSendCheckMessage(false)
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        // the player is dead
        playerData.playerInternalData.isAlive = false
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return true
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val newCarrierList: List<MutableCarrierData> = DataSerializer.copy(carrierList)
        newCarrierList.forEach {
            playerData.playerInternalData.popSystemData().addCarrier(it)
        }
    }
}