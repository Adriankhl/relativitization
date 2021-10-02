package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.data.component.popsystem.MutableCarrierData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.FactoryInternalData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableFactoryData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableFactoryInternalData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.RealString
import relativitization.universe.utils.RelativitizationLogManager

/**
 * Build a factory on player
 *
 * @property senderTopLeaderId the player id of the top leader of the sender
 * @property targetCarrierId build factory on that carrier
 * @property ownerId who own this factory
 * @property factoryInternalData data of the factory
 * @property qualityLevel the quality of the factory, relative to tech level
 * @property storedFuelRestMass fuel stored in the newly built factory
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
        listOf(
            RealString("Build a foreign factory with quality level "),
            IntString(0),
            RealString(" owned by "),
            IntString(1),
            RealString(" at carrier "),
            IntString(2),
            RealString(" of player "),
            IntString(3),
            RealString(". Initial stored fuel rest mass: "),
            IntString(4),
        ),
        listOf(
            qualityLevel.toString(),
            ownerId.toString(),
            targetCarrierId.toString(),
            toId.toString(),
            storedFuelRestMass.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
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


        return CanSendCheckMessage(
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

        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId)

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

        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(targetCarrierId)
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
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Build a factory on player
 *
 * @property outputResourceType the resource type of this factory
 * @property targetCarrierId build factory on that carrier
 * @property qualityLevel the quality of the factory, relative to tech level
 */
@Serializable
data class BuildLocalFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val outputResourceType: ResourceType,
    val targetCarrierId: Int,
    val qualityLevel: Double,
) : Command() {
    override val description: I18NString = I18NString(
        listOf(
            RealString("Build a local factory with quality level "),
            IntString(0),
            RealString(" at carrier "),
            IntString(1),
            RealString(" of player "),
            IntString(2),
        ),
        listOf(
            qualityLevel.toString(),
            targetCarrierId.toString(),
            toId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        val isSubordinateOrSelf: Boolean = playerData.isSubOrdinateOrSelf(toId)
        val isSubordinateOrSelfI18NString: I18NString = if (isSubordinateOrSelf) {
            I18NString("")
        } else {
            I18NString("Not subordinate or self.")
        }

        return CanSendCheckMessage(
            isSubordinateOrSelf,
            I18NString.combine(
                listOf(
                    isSubordinateOrSelfI18NString
                )
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val isLeader: Boolean = playerData.isLeaderOrSelf(fromId)
        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId)

        val requiredFuel: Double = playerData.playerInternalData.playerScienceData().playerScienceProductData.newFactoryFuelNeededByConstruction(
            outputResourceType = outputResourceType,
            qualityLevel = qualityLevel
        )
        val hasFuel: Boolean = playerData.playerInternalData.physicsData().fuelRestMassData.production >- requiredFuel

        return isLeader && hasCarrier && hasFuel
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {

        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                targetCarrierId
            )

        val newFactoryInternalData: MutableFactoryInternalData = playerData.playerInternalData.playerScienceData().playerScienceProductData.newFactoryInternalData(
            outputResourceType = outputResourceType,
            qualityLevel = qualityLevel
        )

        val requiredFuel: Double = playerData.playerInternalData.playerScienceData().playerScienceProductData.newFactoryFuelNeededByConstruction(
            outputResourceType = outputResourceType,
            qualityLevel = qualityLevel
        )

        playerData.playerInternalData.physicsData().fuelRestMassData.production -= requiredFuel

        carrier.allPopData.labourerPopData.addFactory(
            MutableFactoryData(
                ownerPlayerId = toId,
                factoryInternalData = newFactoryInternalData,
                numBuilding = 1,
                isOpened = true,
                lastOutputAmount = 0.0,
                lastInputAmountMap = mutableMapOf(),
                storedFuelRestMass = 0.0,
                lastNumEmployee = 0.0
            )
        )
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}