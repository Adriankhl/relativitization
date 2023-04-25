package relativitization.universe.game.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.game.data.MutablePlayerData
import relativitization.universe.game.data.UniverseSettings
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityData
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.game.data.components.defaults.popsystem.pop.service.export.MutablePlayerExportCenterData
import relativitization.universe.game.data.components.defaults.popsystem.pop.service.export.MutablePlayerSingleExportData
import relativitization.universe.game.data.components.defaults.popsystem.pop.service.export.MutablePopExportCenterData
import relativitization.universe.game.data.components.defaults.popsystem.pop.service.export.MutablePopSingleExportData
import relativitization.universe.game.data.components.economyData
import relativitization.universe.game.data.components.modifierData
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.game.maths.physics.Int4D
import relativitization.universe.game.maths.physics.Intervals
import relativitization.universe.game.utils.I18NString
import relativitization.universe.game.utils.IntString
import relativitization.universe.game.utils.NormalString
import relativitization.universe.game.utils.RelativitizationLogManager
import kotlin.math.pow

/**
 * Send fuel from your storage to another player, also improve relation modifier
 *
 * @property amount the amount of fuel to send
 * @property senderFuelLossFractionPerDistance the loss fraction, determined by player science data
 */
@Serializable
data class SendFuelFromStorageCommand(
    override val toId: Int,
    val amount: Double,
    val senderFuelLossFractionPerDistance: Double,
) : DefaultCommand() {
    override fun name(): String = "Send Fuel (Storage)"
    override fun description(fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Send "),
            IntString(0),
            NormalString(" fuel from player "),
            IntString(1),
            NormalString(" to "),
            IntString(2),
            NormalString(". "),
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
    ): CommandErrorMessage {
        val hasAmount = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.storage >= amount,
            I18NString(
                listOf(
                    NormalString("Storage fuel amount "),
                    IntString(0),
                    NormalString(" is less than "),
                    IntString(1),
                    NormalString(". ")
                ),
                listOf(
                    playerData.playerInternalData.physicsData().fuelRestMassData.storage.toString(),
                    amount.toString()
                )
            )
        )

        val isLossFractionValid = CommandErrorMessage(
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.fuelLogisticsLossFractionPerDistance <= senderFuelLossFractionPerDistance,
            I18NString(
                listOf(
                    NormalString("Sender fuel loss fraction per distance"),
                    IntString(0),
                    NormalString(" is greater than "),
                    IntString(1),
                    NormalString(". ")
                ),
                listOf(
                    playerData.playerInternalData.playerScienceData().playerScienceApplicationData.fuelLogisticsLossFractionPerDistance.toString(),
                    senderFuelLossFractionPerDistance.toString()
                )
            )
        )

        return CommandErrorMessage(
            listOf(
                hasAmount,
                isLossFractionValid
            )
        )
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.physicsData().removeExternalStorageFuel(amount)
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isFuelIncreaseEnable = CommandErrorMessage(
            playerData.playerInternalData.modifierData().physicsModifierData.disableRestMassIncreaseTimeLimit <= 0,
            I18NString("Fuel increase is disabled. ")
        )

        return CommandErrorMessage(
            listOf(
                isFuelIncreaseEnable,
            )
        )
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        val receiverLossFractionPerDistance: Double =
            playerData.playerInternalData.playerScienceData()
                .playerScienceApplicationData.fuelLogisticsLossFractionPerDistance

        val lossFractionPerDistance: Double =
            (receiverLossFractionPerDistance + senderFuelLossFractionPerDistance) * 0.5

        val distance: Int = Intervals.intDistance(
            fromInt4D,
            playerData.int4D
        )

        val remainFraction: Double = if (distance <= Intervals.sameCubeIntDistance()) {
            1.0
        } else {
            (1.0 - lossFractionPerDistance).pow(distance)
        }

        val remainAmount: Double = remainFraction * amount

        // Improve diplomatic relation by sending fuel
        val originalTotalAmount: Double =
            playerData.playerInternalData.physicsData().fuelRestMassData.total()
        val changeFraction: Double = if (originalTotalAmount > 0.0) {
            remainAmount / originalTotalAmount
        } else {
            Double.MAX_VALUE
        }
        val relationChange: Double = if (changeFraction > 1.0) {
            100.0
        } else {
            100.0 * changeFraction
        }
        val duration = 10

        playerData.playerInternalData.modifierData().diplomacyModifierData.addReceiveFuelToRelationModifier(
            otherPlayerId = fromId,
            relationChange = relationChange,
            duration = duration,
        )

        // Add fuel
        playerData.playerInternalData.physicsData().addExternalFuel(remainAmount)
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}


/**
 * Send resource from your storage to another player
 *
 * @property resourceType the type of resource
 * @property resourceQualityClass the quality class of the resource
 * @property resourceQualityData the quality data of the resource
 * @property amount the amount of resource to send
 * @property senderResourceLossFractionPerDistance the loss fraction, determined by player
 *  science data
 */
@Serializable
data class SendResourceFromStorageCommand(
    override val toId: Int,
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
    val resourceQualityData: ResourceQualityData,
    val amount: Double,
    val senderResourceLossFractionPerDistance: Double,
) : DefaultCommand() {
    override fun name(): String = "Send Resource (Storage)"

    override fun description(fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Send "),
            IntString(0),
            NormalString(" "),
            IntString(1),
            NormalString(" (class: "),
            IntString(2),
            NormalString(") from player "),
            IntString(3),
            NormalString(" to "),
            IntString(4),
            NormalString(". "),
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
    ): CommandErrorMessage {
        val hasAmount = CommandErrorMessage(
            playerData.playerInternalData.economyData().resourceData.getStorageResourceAmount(
                resourceType, resourceQualityClass
            ) >= amount,
            I18NString(
                listOf(
                    NormalString("Storage resource amount "),
                    IntString(0),
                    NormalString(" is less than "),
                    IntString(1),
                    NormalString(". ")
                ),
                listOf(
                    playerData.playerInternalData.economyData().resourceData.getStorageResourceAmount(
                        resourceType, resourceQualityClass
                    ).toString(),
                    amount.toString()
                )
            )
        )

        val isQualityValid = CommandErrorMessage(
            playerData.playerInternalData.economyData().resourceData.getResourceQuality(
                resourceType, resourceQualityClass
            ).toResourceQualityData().squareDiff(resourceQualityData) <= 0.1,
            I18NString(
                listOf(
                    NormalString("Resource quality "),
                    IntString(0),
                    NormalString(" is not similar to "),
                    IntString(1),
                    NormalString(". ")
                ),
                listOf(
                    playerData.playerInternalData.economyData().resourceData.getResourceQuality(
                        resourceType, resourceQualityClass
                    ).toResourceQualityData().toString(),
                    resourceQualityData.toString(),
                )
            )
        )


        val isLossFractionValid = CommandErrorMessage(
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData
                .resourceLogisticsLossFractionPerDistance <= senderResourceLossFractionPerDistance,
            I18NString(
                listOf(
                    NormalString("Sender resource loss fraction per distance"),
                    IntString(0),
                    NormalString(" is greater than "),
                    IntString(1),
                    NormalString(". ")
                ),
                listOf(
                    playerData.playerInternalData.playerScienceData().playerScienceApplicationData
                        .resourceLogisticsLossFractionPerDistance.toString(),
                    senderResourceLossFractionPerDistance.toString()
                )
            )
        )

        return CommandErrorMessage(
            listOf(
                hasAmount,
                isQualityValid,
                isLossFractionValid,
            )
        )
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.economyData().resourceData.getResourceAmountData(
            resourceType, resourceQualityClass
        ).storage -= amount
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        return CommandErrorMessage(true)
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        val receiverLossFractionPerDistance: Double =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData
                .resourceLogisticsLossFractionPerDistance

        val lossFractionPerDistance: Double =
            (receiverLossFractionPerDistance + senderResourceLossFractionPerDistance) * 0.5

        val distance: Int = Intervals.intDistance(
            fromInt4D,
            playerData.int4D
        )

        val remainFraction: Double = if (distance <= Intervals.sameCubeIntDistance()) {
            1.0
        } else {
            (1.0 - lossFractionPerDistance).pow(distance)
        }

        playerData.playerInternalData.economyData().resourceData.addResource(
            newResourceType = resourceType,
            newResourceQuality = resourceQualityData.toMutableResourceQualityData(),
            newResourceAmount = amount * remainFraction
        )
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Send fuel to a player, should be sent by mechanism only
 *
 * @property amount the amount of resource to send
 * @property senderFuelLossFractionPerDistance the loss fraction, determined by player science data
 */
@Serializable
data class SendFuelCommand(
    override val toId: Int,
    val amount: Double,
    val senderFuelLossFractionPerDistance: Double,
) : DefaultCommand() {
    override fun name(): String = "Send Fuel"

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(false)

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isFuelIncreaseEnable = CommandErrorMessage(
            playerData.playerInternalData.modifierData().physicsModifierData.disableRestMassIncreaseTimeLimit <= 0,
            I18NString("Fuel increase is disabled. ")
        )

        return CommandErrorMessage(
            listOf(
                isFuelIncreaseEnable,
            )
        )
    }


    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        val receiverLossFractionPerDistance: Double =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.fuelLogisticsLossFractionPerDistance

        val lossFractionPerDistance: Double =
            (receiverLossFractionPerDistance + senderFuelLossFractionPerDistance) * 0.5

        val distance: Int = Intervals.intDistance(
            fromInt4D,
            playerData.int4D
        )

        val remainFraction: Double = if (distance <= Intervals.sameCubeIntDistance()) {
            1.0
        } else {
            (1.0 - lossFractionPerDistance).pow(distance)
        }

        playerData.playerInternalData.physicsData().addExternalFuel(amount * remainFraction)
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}


/**
 * Send resource to a player, should be sent by mechanism only
 *
 * @property resourceType the type of resource to send
 * @property resourceQualityData the quality of the resource to send
 * @property amount the amount of resource to send
 * @property senderResourceLossFractionPerDistance the loss fraction, determined by player
 *  science data
 */
@Serializable
data class SendResourceCommand(
    override val toId: Int,
    val resourceType: ResourceType,
    val resourceQualityData: ResourceQualityData,
    val amount: Double,
    val senderResourceLossFractionPerDistance: Double,
) : DefaultCommand() {
    override fun name(): String = "Send Resource"

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(false)

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        val receiverLossFractionPerDistance: Double = playerData.playerInternalData
            .playerScienceData().playerScienceApplicationData
            .resourceLogisticsLossFractionPerDistance

        val lossFractionPerDistance: Double =
            (receiverLossFractionPerDistance + senderResourceLossFractionPerDistance) * 0.5

        val distance: Int = Intervals.intDistance(
            fromInt4D,
            playerData.int4D
        )

        val remainFraction: Double = if (distance <= Intervals.sameCubeIntDistance()) {
            1.0
        } else {
            (1.0 - lossFractionPerDistance).pow(distance)
        }

        playerData.playerInternalData.economyData().resourceData.addResource(
            newResourceType = resourceType,
            newResourceQuality = resourceQualityData.toMutableResourceQualityData(),
            newResourceAmount = amount * remainFraction
        )
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}


/**
 * Send resource to a pop, should be sent by mechanism only
 *
 * @property targetCarrierId the id of the carrier where the pop is located
 * @property targetPopType the type of the pop
 * @property resourceType the type of resource to send
 * @property resourceQualityData the quality of the resource to send
 * @property amount the amount of resource to send
 * @property senderResourceLossFractionPerDistance the loss fraction, determined by player
 *  science data
 */
@Serializable
data class SendResourceToPopCommand(
    override val toId: Int,
    val targetCarrierId: Int,
    val targetPopType: PopType,
    val resourceType: ResourceType,
    val resourceQualityData: ResourceQualityData,
    val amount: Double,
    val senderResourceLossFractionPerDistance: Double,
) : DefaultCommand() {
    override fun name(): String = "Send Resource (Pop)"

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(false)

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        val receiverLossFractionPerDistance: Double = playerData.playerInternalData
            .playerScienceData().playerScienceApplicationData
            .resourceLogisticsLossFractionPerDistance

        val lossFractionPerDistance: Double =
            (receiverLossFractionPerDistance + senderResourceLossFractionPerDistance) * 0.5

        val distance: Int = Intervals.intDistance(
            fromInt4D,
            playerData.int4D
        )

        val remainFraction: Double = if (distance <= Intervals.sameCubeIntDistance()) {
            1.0
        } else {
            (1.0 - lossFractionPerDistance).pow(distance)
        }

        val carrierDataMap = playerData.playerInternalData.popSystemData().carrierDataMap

        if (carrierDataMap.containsKey(targetCarrierId)) {
            carrierDataMap.getValue(targetCarrierId).allPopData.addDesireResource(
                popType = targetPopType,
                resourceType = resourceType,
                resourceQualityData = resourceQualityData,
                resourceAmount = amount * remainFraction,
            )
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Send fuel to PopExportCenter to buy resource, should be sent by mechanism only
 *
 * @property fromCarrierId sent from the carrier with this id
 * @property fromPopType sent from this pop type
 * @property targetTopLeaderId the top leader id of the target player
 * @property targetCarrierId the id of the carrier to build the pop export center
 * @property resourceType the type of resource to buy
 * @property resourceQualityClass the quality class of the resource to buy
 * @property fuelRestMassAmount the amount of fuel to the center to buy the resource
 * @property amountPerTime the amount of resource to buy per time
 * @property senderFuelLossFractionPerDistance the loss fraction, determined by player science data
 */
@Serializable
data class PopBuyResourceCommand(
    override val toId: Int,
    val fromCarrierId: Int,
    val fromPopType: PopType,
    val targetTopLeaderId: Int,
    val targetCarrierId: Int,
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
    val fuelRestMassAmount: Double,
    val amountPerTime: Double,
    val senderFuelLossFractionPerDistance: Double,
) : DefaultCommand() {
    override fun name(): String = "Pop Buy Resource"

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage = CommandErrorMessage(false)

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val validTopLeaderId = CommandErrorMessage(
            playerData.topLeaderId() == targetTopLeaderId,
            I18NString("Top leader id is wrong. ")
        )

        val isFuelIncreaseEnable = CommandErrorMessage(
            playerData.playerInternalData.modifierData().physicsModifierData.disableRestMassIncreaseTimeLimit <= 0,
            I18NString("Fuel increase is disabled. ")
        )

        return CommandErrorMessage(
            listOf(
                validTopLeaderId,
                isFuelIncreaseEnable,
            )
        )
    }


    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        val receiverLossFractionPerDistance: Double =
            playerData.playerInternalData.playerScienceData()
                .playerScienceApplicationData.fuelLogisticsLossFractionPerDistance

        val lossFractionPerDistance: Double =
            (receiverLossFractionPerDistance + senderFuelLossFractionPerDistance) * 0.5

        val distance: Int = Intervals.intDistance(
            fromInt4D,
            playerData.int4D
        )

        val remainFraction: Double = if (distance <= Intervals.sameCubeIntDistance()) {
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
 * Send fuel to player export center to buy resource
 *
 * @property targetTopLeaderId top leader id of the target player
 * @property targetCarrierId build export center at that carrier
 * @property targetPlayerIdOfExportCenter export to this player
 * @property resourceType type of the resource
 * @property fuelRestMassAmount fuel rest mass to buy resource
 * @property amountPerTime the amount to buy per turn
 * @property senderFuelLossFractionPerDistance the logistics loss per distance
 */
@Serializable
data class PlayerBuyResourceCommand(
    override val toId: Int,
    val targetTopLeaderId: Int,
    val targetCarrierId: Int,
    val targetPlayerIdOfExportCenter: Int,
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
    val fuelRestMassAmount: Double,
    val amountPerTime: Double,
    val senderFuelLossFractionPerDistance: Double,
) : DefaultCommand() {
    override fun name(): String = "Player Buy Resource"

    override fun description(fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Send "),
            IntString(0),
            NormalString(" fuel from player "),
            IntString(1),
            NormalString(" to "),
            IntString(2),
            NormalString(" to buy "),
            IntString(3),
            NormalString(" "),
            IntString(4),
            NormalString(" of class "),
            IntString(5),
            NormalString(" per time.")
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
    ): CommandErrorMessage {
        // Whether the receiver has the same top leader
        val sameTopLeaderId: Boolean = (playerData.topLeaderId() == targetTopLeaderId)

        // Compute import tariff
        val tariffFactor: Double = if (sameTopLeaderId) {
            1.0
        } else {
            1.0 + playerData.playerInternalData.economyData().taxData.taxRateData.importTariff
                .getResourceTariffRate(
                    topLeaderId = targetTopLeaderId, resourceType = resourceType
                )
        }

        val hasAmount = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.trade >=
                    fuelRestMassAmount * tariffFactor,
            I18NString(
                listOf(
                    NormalString("Trade fuel amount "),
                    IntString(0),
                    NormalString(" is less than "),
                    IntString(1),
                    NormalString(". ")
                ),
                listOf(
                    playerData.playerInternalData.physicsData().fuelRestMassData.trade.toString(),
                    fuelRestMassAmount.toString()
                )
            )
        )

        val isLossFractionValid = CommandErrorMessage(
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData
                .fuelLogisticsLossFractionPerDistance <= senderFuelLossFractionPerDistance,
            I18NString(
                listOf(
                    NormalString("Sender fuel loss fraction per distance"),
                    IntString(0),
                    NormalString(" is greater than "),
                    IntString(1),
                    NormalString(". ")
                ),
                listOf(
                    playerData.playerInternalData.playerScienceData().playerScienceApplicationData
                        .fuelLogisticsLossFractionPerDistance.toString(),
                    senderFuelLossFractionPerDistance.toString()
                )
            )
        )

        return CommandErrorMessage(
            listOf(
                hasAmount,
                isLossFractionValid,
            )
        )
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        // Whether the receiver has the same top leader
        val sameTopLeaderId: Boolean = (playerData.topLeaderId() == targetTopLeaderId)

        // Compute import tariff
        val tariffFactor: Double = if (sameTopLeaderId) {
            1.0
        } else {
            1.0 + playerData.playerInternalData.economyData().taxData.taxRateData.importTariff
                .getResourceTariffRate(
                    topLeaderId = targetTopLeaderId, resourceType = resourceType
                )
        }

        // Consume resource
        playerData.playerInternalData.physicsData()
            .removeExternalTradeFuel(fuelRestMassAmount * tariffFactor)

        // Add tariff to storage
        playerData.playerInternalData.economyData().taxData.storedFuelRestMass +=
            fuelRestMassAmount * (tariffFactor - 1.0) / tariffFactor
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val validTopLeaderId = CommandErrorMessage(
            playerData.topLeaderId() == targetTopLeaderId,
            I18NString("Top leader id is wrong. ")
        )

        val isFuelIncreaseEnable = CommandErrorMessage(
            playerData.playerInternalData.modifierData().physicsModifierData.disableRestMassIncreaseTimeLimit <= 0,
            I18NString("Fuel increase is disabled. ")
        )

        return CommandErrorMessage(
            listOf(
                validTopLeaderId,
                isFuelIncreaseEnable,
            )
        )
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        val receiverLossFractionPerDistance: Double = playerData.playerInternalData
            .playerScienceData().playerScienceApplicationData.fuelLogisticsLossFractionPerDistance

        val lossFractionPerDistance: Double =
            (receiverLossFractionPerDistance + senderFuelLossFractionPerDistance) * 0.5

        val distance: Int = Intervals.intDistance(
            fromInt4D,
            playerData.int4D
        )

        val remainFraction: Double = if (distance <= Intervals.sameCubeIntDistance()) {
            1.0
        } else {
            (1.0 - lossFractionPerDistance).pow(distance)
        }

        val carrierDataMap = playerData.playerInternalData.popSystemData().carrierDataMap

        if (carrierDataMap.containsKey(targetCarrierId)) {

            val exportCenterMap: MutableMap<Int, MutablePlayerExportCenterData> =
                carrierDataMap.getValue(
                    targetCarrierId
                ).allPopData.servicePopData.exportData.playerExportCenterMap

            val centerData: MutablePlayerSingleExportData = exportCenterMap.getOrPut(
                fromId
            ) {
                MutablePlayerExportCenterData()
            }.getSingleExportData(
                targetPlayerId = targetPlayerIdOfExportCenter,
                resourceType = resourceType,
                resourceQualityClass = resourceQualityClass
            )

            centerData.amountPerTime = amountPerTime
            centerData.storedFuelRestMass += fuelRestMassAmount * remainFraction
        } else {
            logger.debug("No such carrier")
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}