package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.component.economy.ResourceQualityData
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.data.component.popsystem.pop.labourer.factory.InputResourceData
import relativitization.universe.utils.I18NString

/**
 * Build a factory on player
 *
 * @property topLeaderId the player id of the top leader of the sender
 */
@Serializable
data class BuildFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val topLeaderId: Int,
    val outputResource: ResourceType = ResourceType.FUEL,
    val maxOutputResourceQualityData: ResourceQualityData = ResourceQualityData(),
    val maxOutputAmount: Double = 0.0,
    val inputResourceMap: Map<ResourceType, InputResourceData> = mapOf(),
    val fuelRestMassConsumptionRate: Double = 0.0,
    val storedFuelRestMass: Double = 0.0,
    val maxNumEmployee: Double = 0.0,
    val size: Double = 0.0,
) : Command() {
    override val description: I18NString = I18NString(
        listOf(),
        listOf()
    )

    override fun canSend(playerData: MutablePlayerData, universeSettings: UniverseSettings): CanSendWithMessage {
        val sameTopLeaderId: Boolean = playerData.topLeaderId() == topLeaderId
        return if(sameTopLeaderId) {
            CanSendWithMessage(true)
        } else {
            CanSendWithMessage(
                false,
                CanSendWIthMessageI18NStringFactory.isTopLeaderIdWrong(playerData.topLeaderId(), topLeaderId)
            )
        }
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val sameTopLeader: Boolean = playerData.topLeaderId() == topLeaderId
        return sameTopLeader || playerData.playerInternalData.politicsData().allowForeignInvestor
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData
    }
}