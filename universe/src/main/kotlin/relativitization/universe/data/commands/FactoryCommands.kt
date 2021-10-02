package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.data.component.popsystem.MutableCarrierData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.FactoryInternalData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableFactoryData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.RealString
import relativitization.universe.utils.RelativitizationLogManager

/**
 * Build a factory on player
 *
 * @property senderTopLeaderId the player id of the top leader of the sender
 */
@Serializable
data class BuildForeignFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val senderTopLeaderId: Int,
    val targetCarrierId: Int,
    val ownerId: Int,
    val factoryInternalData: FactoryInternalData,
    val qualityLevel: Double,
    val storedFuelRestMass: Double,
) : Command() {
    override val description: I18NString = I18NString(
        listOf(),
        listOf()
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendWithMessage {
        val sameTopLeaderId: Boolean = playerData.topLeaderId() == senderTopLeaderId
        val sameTopLeaderIdI18NString: I18NString = if (sameTopLeaderId) {
            I18NString("")
        } else {
            I18NString(
                listOf(
                    RealString("Top leader id "),
                    IntString(0),
                    RealString(" is not equal to "),
                    IntString(1),
                    RealString(". ")
                ),
                listOf(
                    senderTopLeaderId.toString(),
                    playerData.topLeaderId().toString(),
                ),
            )
        }

        val validFactoryInternalData: Boolean = factoryInternalData.squareDiff(
            playerData.playerInternalData.playerScienceData().playerScienceProductData.newFactoryInternalData(
                factoryInternalData.outputResource,
                qualityLevel
            )
        ) < 0.1
        val validFactoryInternalDataI18NString: I18NString = if (sameTopLeaderId) {
            I18NString("")
        } else {
            I18NString("Factory internal data is not valid. ")
        }

        val fuelNeeded: Double =
            storedFuelRestMass + playerData.playerInternalData.playerScienceData().playerScienceProductData.newFactoryFuelNeededByConstruction(
                factoryInternalData.outputResource,
                qualityLevel
            )
        val enoughFuelRestMass: Boolean =
            playerData.playerInternalData.physicsData().fuelRestMassData.production >= fuelNeeded
        val enoughFuelRestMassI18NString: I18NString = if (enoughFuelRestMass) {
            I18NString("")
        } else {
            I18NString("Not enough fuel rest mass. ")
        }


        return CanSendWithMessage(
            sameTopLeaderId && validFactoryInternalData && enoughFuelRestMass,
            I18NString.combine(
                listOf(
                    sameTopLeaderIdI18NString,
                    validFactoryInternalDataI18NString,
                    enoughFuelRestMassI18NString
                )
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val sameTopLeader: Boolean = playerData.topLeaderId() == senderTopLeaderId
        val allowConstruction: Boolean =
            (sameTopLeader || playerData.playerInternalData.politicsData().allowForeignInvestor)

        val hasCarrier: Boolean = playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId)

        return allowConstruction && hasCarrier
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        val fuelNeeded: Double =
            storedFuelRestMass + playerData.playerInternalData.playerScienceData().playerScienceProductData.newFactoryFuelNeededByConstruction(
                factoryInternalData.outputResource,
                qualityLevel
            )
        playerData.playerInternalData.physicsData().fuelRestMassData.production -= fuelNeeded

    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {

        val hasCarrier: Boolean = playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId)

        if (hasCarrier) {
            val carrier: MutableCarrierData = playerData.playerInternalData.popSystemData().carrierDataMap.getValue(targetCarrierId)
            carrier.allPopData.labourerPopData.addFactory(
                MutableFactoryData(
                    ownerPlayerId = ownerId,
                    factoryInternalData = DataSerializer.copy(factoryInternalData),
                    numBuilding = 1,
                    isOpened = true,
                    lastOutputAmount = 0.0,
                    lastInputAmountMap = mutableMapOf(),
                    storedFuelRestMass = storedFuelRestMass,
                    lastNumEmployee = 0.0
                )
            )
        } else {
            logger.error("No carrier $targetCarrierId")
        }
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}