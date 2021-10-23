package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.component.economy.ResourceQualityClass
import relativitization.universe.data.component.economy.ResourceQualityData
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.data.component.popsystem.pop.PopType
import relativitization.universe.data.component.popsystem.pop.service.export.MutablePopExportCenterData
import relativitization.universe.data.component.popsystem.pop.service.export.MutablePopSingleExportData
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.RealString
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.pow

/**
 * Send resource from yourself to another player
 */
@Serializable
data class SendResourceFromStorageCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
    val resourceQualityData: ResourceQualityData,
    val amount: Double,
    val senderResourceLossFractionPerDistance: Double,
) : Command() {
    override val description: I18NString = I18NString(
        listOf(
            RealString("Send "),
            IntString(0),
            RealString(" "),
            IntString(1),
            RealString(" (class: "),
            IntString(2),
            RealString(") from player "),
            IntString(3),
            RealString(" to "),
            IntString(4),
        ),
        listOf(
            amount.toString(),
            resourceType.toString(),
            resourceQualityClass.toString(),
            fromId.toString(),
            toId.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        val hasAmount: Boolean =
            playerData.playerInternalData.economyData().resourceData.getTradeResourceAmount(
                resourceType, resourceQualityClass
            ) >= amount
        val hasAmountI18NString: I18NString = if (hasAmount) {
            I18NString("")
        } else {
            I18NString(
                listOf(
                    RealString("Trade resource amount "),
                    IntString(0),
                    RealString(" is less than "),
                    IntString(1),
                    RealString(". ")
                ),
                listOf(
                    playerData.playerInternalData.economyData().resourceData.getTradeResourceAmount(
                        resourceType, resourceQualityClass
                    ).toString(),
                    amount.toString()
                )
            )
        }

        val isQualityValid: Boolean =
            playerData.playerInternalData.economyData().resourceData.getResourceQuality(
                resourceType, resourceQualityClass
            ).toResourceQualityData().squareDiff(resourceQualityData) <= 0.1
        val isQualityValidI18NString: I18NString = if (isQualityValid) {
            I18NString("")
        } else {
            I18NString(
                listOf(
                    RealString("Resource quality "),
                    IntString(0),
                    RealString(" is not similar to "),
                    IntString(1),
                    RealString(". ")
                ),
                listOf(
                    playerData.playerInternalData.economyData().resourceData.getResourceQuality(
                        resourceType, resourceQualityClass
                    ).toResourceQualityData().toString(),
                    resourceQualityData.toString(),
                )
            )
        }

        val isLossFractionValid: Boolean =
            playerData.playerInternalData.playerScienceData().playerScienceProductData.resourceLogisticsLossFractionPerDistance <= senderResourceLossFractionPerDistance
        val isLossFractionValidI18NString: I18NString = if (isLossFractionValid) {
            I18NString("")
        } else {
            I18NString(
                listOf(
                    RealString("Sender resource loss fraction per distance"),
                    IntString(0),
                    RealString(" is greater than "),
                    IntString(1),
                    RealString(". ")
                ),
                listOf(
                    playerData.playerInternalData.playerScienceData().playerScienceProductData.resourceLogisticsLossFractionPerDistance.toString(),
                    senderResourceLossFractionPerDistance.toString()
                )
            )
        }

        return CanSendCheckMessage(
            hasAmount && isQualityValid && isLossFractionValid,
            I18NString.combine(
                listOf(
                    hasAmountI18NString,
                    isQualityValidI18NString,
                    isLossFractionValidI18NString,
                )
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return true
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.economyData().resourceData.getResourceAmountData(
            resourceType, resourceQualityClass
        ).trade -= amount
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val receiverLossFractionPerDistance: Double =
            playerData.playerInternalData.playerScienceData().playerScienceProductData.resourceLogisticsLossFractionPerDistance

        val lossFractionPerDistance: Double =
            (receiverLossFractionPerDistance + senderResourceLossFractionPerDistance) * 0.5

        val distance: Double = Intervals.distance(
            fromInt4D.toDouble3D(),
            playerData.int4D.toDouble3D()
        )

        val remainFraction: Double = if (distance < 1.0) {
            1.0
        } else {
            (1.0 - lossFractionPerDistance).pow(distance)
        }

        playerData.playerInternalData.economyData().resourceData.addNewResource(
            resourceType = resourceType,
            newResourceQuality = resourceQualityData.toMutableResourceQualityData(),
            amount = amount * remainFraction
        )
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Send fuel from yourself to another player
 */
@Serializable
data class SendFuelFromStorageCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val amount: Double,
    val senderFuelLossFractionPerDistance: Double,
) : Command() {
    override val description: I18NString = I18NString(
        listOf(
            RealString("Send "),
            IntString(0),
            RealString(" fuel from player "),
            IntString(1),
            RealString(" to "),
            IntString(2),
        ),
        listOf(
            amount.toString(),
            fromId.toString(),
            toId.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        val hasAmount: Boolean =
            playerData.playerInternalData.physicsData().fuelRestMassData.trade >= amount
        val hasAmountI18NString: I18NString = if (hasAmount) {
            I18NString("")
        } else {
            I18NString(
                listOf(
                    RealString("Trade fuel amount "),
                    IntString(0),
                    RealString(" is less than "),
                    IntString(1),
                    RealString(". ")
                ),
                listOf(
                    playerData.playerInternalData.physicsData().fuelRestMassData.trade.toString(),
                    amount.toString()
                )
            )
        }

        val isLossFractionValid: Boolean =
            playerData.playerInternalData.playerScienceData().playerScienceProductData.fuelLogisticsLossFractionPerDistance <= senderFuelLossFractionPerDistance
        val isLossFractionValidI18NString: I18NString = if (isLossFractionValid) {
            I18NString("")
        } else {
            I18NString(
                listOf(
                    RealString("Sender fuel loss fraction per distance"),
                    IntString(0),
                    RealString(" is greater than "),
                    IntString(1),
                    RealString(". ")
                ),
                listOf(
                    playerData.playerInternalData.playerScienceData().playerScienceProductData.fuelLogisticsLossFractionPerDistance.toString(),
                    senderFuelLossFractionPerDistance.toString()
                )
            )
        }

        return CanSendCheckMessage(
            hasAmount && isLossFractionValid,
            I18NString.combine(
                listOf(
                    hasAmountI18NString,
                    isLossFractionValidI18NString,
                )
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return playerData.playerInternalData.modifierData().physicsModifierData.disableRestMassIncreaseTimeLimit <= 0
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.physicsData().fuelRestMassData.trade -= amount
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val receiverLossFractionPerDistance: Double =
            playerData.playerInternalData.playerScienceData().playerScienceProductData.fuelLogisticsLossFractionPerDistance

        val lossFractionPerDistance: Double =
            (receiverLossFractionPerDistance + senderFuelLossFractionPerDistance) * 0.5

        val distance: Double = Intervals.distance(
            fromInt4D.toDouble3D(),
            playerData.int4D.toDouble3D()
        )

        val remainFraction: Double = if (distance < 1.0) {
            1.0
        } else {
            (1.0 - lossFractionPerDistance).pow(distance)
        }

        playerData.playerInternalData.physicsData().addFuel(amount * remainFraction)
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Generic send resource, cannot be sent by player directly
 */
@Serializable
data class SendResourceCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val resourceType: ResourceType,
    val resourceQualityData: ResourceQualityData,
    val amount: Double,
    val senderResourceLossFractionPerDistance: Double,
) : Command() {
    override val description: I18NString = I18NString("")

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage = CanSendCheckMessage(false)

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return true
    }


    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val receiverLossFractionPerDistance: Double =
            playerData.playerInternalData.playerScienceData().playerScienceProductData.resourceLogisticsLossFractionPerDistance

        val lossFractionPerDistance: Double =
            (receiverLossFractionPerDistance + senderResourceLossFractionPerDistance) * 0.5

        val distance: Double = Intervals.distance(
            fromInt4D.toDouble3D(),
            playerData.int4D.toDouble3D()
        )

        val remainFraction: Double = if (distance < 1.0) {
            1.0
        } else {
            (1.0 - lossFractionPerDistance).pow(distance)
        }

        playerData.playerInternalData.economyData().resourceData.addNewResource(
            resourceType = resourceType,
            newResourceQuality = resourceQualityData.toMutableResourceQualityData(),
            amount = amount * remainFraction
        )
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Generic send fuel, cannot be sent by player directly
 */
@Serializable
data class SendFuelCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val amount: Double,
    val senderFuelLossFractionPerDistance: Double,
) : Command() {
    override val description: I18NString = I18NString("")

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage = CanSendCheckMessage(false)

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return true
    }


    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val receiverLossFractionPerDistance: Double =
            playerData.playerInternalData.playerScienceData().playerScienceProductData.fuelLogisticsLossFractionPerDistance

        val lossFractionPerDistance: Double =
            (receiverLossFractionPerDistance + senderFuelLossFractionPerDistance) * 0.5

        val distance: Double = Intervals.distance(
            fromInt4D.toDouble3D(),
            playerData.int4D.toDouble3D()
        )

        val remainFraction: Double = if (distance < 1.0) {
            1.0
        } else {
            (1.0 - lossFractionPerDistance).pow(distance)
        }

        playerData.playerInternalData.physicsData().addFuel(amount * remainFraction)
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Send resource to a pop
 * Should be handled by mechanism only, cannot be sent by player manually
 */
@Serializable
data class SendResourceToPopCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetCarrierId: Int,
    val targetPopType: PopType,
    val resourceType: ResourceType,
    val resourceQualityData: ResourceQualityData,
    val amount: Double,
    val senderResourceLossFractionPerDistance: Double,
) : Command() {
    override val description: I18NString = I18NString("")

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage = CanSendCheckMessage(false)

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return true
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val receiverLossFractionPerDistance: Double =
            playerData.playerInternalData.playerScienceData().playerScienceProductData.resourceLogisticsLossFractionPerDistance

        val lossFractionPerDistance: Double =
            (receiverLossFractionPerDistance + senderResourceLossFractionPerDistance) * 0.5

        val distance: Double = Intervals.distance(
            fromInt4D.toDouble3D(),
            playerData.int4D.toDouble3D()
        )

        val remainFraction: Double = if (distance < 1.0) {
            1.0
        } else {
            (1.0 - lossFractionPerDistance).pow(distance)
        }

        val carrierDataMap = playerData.playerInternalData.popSystemData().carrierDataMap

        if (carrierDataMap.containsKey(targetCarrierId)) {
            carrierDataMap.getValue(targetCarrierId).allPopData.addResource(
                popType = targetPopType,
                resourceType = resourceType,
                resourceQualityData = resourceQualityData,
                resourceAmount = amount,
            )
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Send fuel to PopExportCenter to buy resource
 * Should be handled by mechanism only, cannot be sent by player manually
 */
@Serializable
data class PopBuyResourceCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val fromCarrierId: Int,
    val fromPopType: PopType,
    val targetCarrierId: Int,
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
    val fuelRestMassAmount: Double,
    val amountPerTime: Double,
    val senderFuelLossFractionPerDistance: Double,
) : Command() {
    override val description: I18NString = I18NString("")

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage = CanSendCheckMessage(false)

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return true
    }


    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val receiverLossFractionPerDistance: Double =
            playerData.playerInternalData.playerScienceData().playerScienceProductData.fuelLogisticsLossFractionPerDistance

        val lossFractionPerDistance: Double =
            (receiverLossFractionPerDistance + senderFuelLossFractionPerDistance) * 0.5

        val distance: Double = Intervals.distance(
            fromInt4D.toDouble3D(),
            playerData.int4D.toDouble3D()
        )

        val remainFraction: Double = if (distance < 1.0) {
            1.0
        } else {
            (1.0 - lossFractionPerDistance).pow(distance)
        }

        val carrierDataMap = playerData.playerInternalData.popSystemData().carrierDataMap

        if (carrierDataMap.containsKey(targetCarrierId)) {

            val exportCenterMap: MutableMap<Int, MutablePopExportCenterData> =
                carrierDataMap.getValue(
                    targetCarrierId
                ).allPopData.servicePopData.exportData.popExportCenterMap

            val centerData: MutablePopSingleExportData = exportCenterMap.getOrPut(
                fromId
            ) {
                MutablePopExportCenterData()
            }.getSingleExportData(
                playerId = fromId,
                carrierId = fromCarrierId,
                popType = fromPopType,
                resourceType = resourceType,
                resourceQualityClass = resourceQualityClass
            )

            centerData.amountPerTime = amountPerTime
            centerData.storedFuelRestMass += fuelRestMassAmount * remainFraction
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Send fuel from yourself to another player
 */
@Serializable
data class PlayerBuyResourceCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val buyResourceTargetId: Int,
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
    val fuelRestMassAmount: Double,
    val amountPerTime: Double,
    val senderFuelLossFractionPerDistance: Double,
) : Command() {
    override val description: I18NString = I18NString(
        listOf(
            RealString("Send "),
            IntString(0),
            RealString(" fuel from player "),
            IntString(1),
            RealString(" to "),
            IntString(2),
            RealString(" to buy "),
            IntString(3),
            RealString(" "),
            IntString(4),
            RealString(" of class "),
            IntString(5),
            RealString(" per time.")
        ),
        listOf(
            fuelRestMassAmount.toString(),
            fromId.toString(),
            toId.toString(),
            amountPerTime.toString(),
            resourceType.toString(),
            resourceQualityClass.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        val hasAmount: Boolean =
            playerData.playerInternalData.physicsData().fuelRestMassData.trade >= fuelRestMassAmount
        val hasAmountI18NString: I18NString = if (hasAmount) {
            I18NString("")
        } else {
            I18NString(
                listOf(
                    RealString("Trade fuel amount "),
                    IntString(0),
                    RealString(" is less than "),
                    IntString(1),
                    RealString(". ")
                ),
                listOf(
                    playerData.playerInternalData.physicsData().fuelRestMassData.trade.toString(),
                    fuelRestMassAmount.toString()
                )
            )
        }

        val isLossFractionValid: Boolean =
            playerData.playerInternalData.playerScienceData().playerScienceProductData.fuelLogisticsLossFractionPerDistance <= senderFuelLossFractionPerDistance
        val isLossFractionValidI18NString: I18NString = if (isLossFractionValid) {
            I18NString("")
        } else {
            I18NString(
                listOf(
                    RealString("Sender fuel loss fraction per distance"),
                    IntString(0),
                    RealString(" is greater than "),
                    IntString(1),
                    RealString(". ")
                ),
                listOf(
                    playerData.playerInternalData.playerScienceData().playerScienceProductData.fuelLogisticsLossFractionPerDistance.toString(),
                    senderFuelLossFractionPerDistance.toString()
                )
            )
        }

        return CanSendCheckMessage(
            hasAmount && isLossFractionValid,
            I18NString.combine(
                listOf(
                    hasAmountI18NString,
                    isLossFractionValidI18NString,
                )
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return playerData.playerInternalData.modifierData().physicsModifierData.disableRestMassIncreaseTimeLimit <= 0
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.physicsData().fuelRestMassData.trade -= amount
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val receiverLossFractionPerDistance: Double =
            playerData.playerInternalData.playerScienceData().playerScienceProductData.fuelLogisticsLossFractionPerDistance

        val lossFractionPerDistance: Double =
            (receiverLossFractionPerDistance + senderFuelLossFractionPerDistance) * 0.5

        val distance: Double = Intervals.distance(
            fromInt4D.toDouble3D(),
            playerData.int4D.toDouble3D()
        )

        val remainFraction: Double = if (distance < 1.0) {
            1.0
        } else {
            (1.0 - lossFractionPerDistance).pow(distance)
        }

        playerData.playerInternalData.physicsData().addFuel(amount * remainFraction)
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}