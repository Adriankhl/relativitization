package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.defaults.economy.MutableResourceAmountData
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.economy.ResourceQualityData
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.IntTranslateString
import relativitization.universe.utils.NormalString

/**
 * Change import tariff rate of self
 *
 * @property resourceType the tariff of this resource
 * @property rate the new tariff rate
 */
@Serializable
data class ChangeDefaultImportTariffCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val resourceType: ResourceType,
    val rate: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Change default import tariff rate of "),
            IntTranslateString(0),
            NormalString(" to "),
            IntString(1),
            NormalString(". ")
        ),
        listOf(
            resourceType.toString(),
            rate.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {

        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        }

        val isTopLeader: Boolean = playerData.isTopLeader()
        val isTopLeaderI18NString: I18NString = if (isTopLeader) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotTopLeader(playerData.playerId)
        }

        return CommandMessage(
            isSelf && isTopLeader,
            listOf(
                isSelfI18NString,
                isTopLeaderI18NString,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return (fromId == playerData.playerId)
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.economyData().taxData.taxRateData
            .importTariff.defaultTariffRate.resourceTariffRateMap[resourceType] = rate
    }
}

/**
 * Change export tariff rate of self
 *
 * @property resourceType the tariff of this resource
 * @property rate the new tariff rate
 */
@Serializable
data class ChangeDefaultExportTariffCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val resourceType: ResourceType,
    val rate: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Change default export tariff rate of "),
            IntTranslateString(0),
            NormalString(" to "),
            IntString(1),
            NormalString(". ")
        ),
        listOf(
            resourceType.toString(),
            rate.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {

        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        }

        val isTopLeader: Boolean = playerData.isTopLeader()
        val isTopLeaderI18NString: I18NString = if (isTopLeader) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotTopLeader(playerData.playerId)
        }

        return CommandMessage(
            isSelf && isTopLeader,
            listOf(
                isSelfI18NString,
                isTopLeaderI18NString,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return (fromId == playerData.playerId)
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.economyData().taxData.taxRateData
            .exportTariff.defaultTariffRate.resourceTariffRateMap[resourceType] = rate
    }
}

/**
 * Change the low income tax
 *
 * @property rate the new tax rate
 */
@Serializable
data class ChangeLowIncomeTaxCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val rate: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Change low income tax rate to "),
            IntString(0),
            NormalString(". ")
        ),
        listOf(
            rate.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {

        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        }

        val isTopLeader: Boolean = playerData.isTopLeader()
        val isTopLeaderI18NString: I18NString = if (isTopLeader) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotTopLeader(playerData.playerId)
        }

        val isRateValid: Boolean = (rate >= 0.0) && (rate <= 1.0)
        val isRateValidI18NString: I18NString = if (isRateValid) {
            I18NString("")
        } else {
            I18NString("Rate should be between 0 and 1. ")
        }

        return CommandMessage(
            isSelf && isTopLeader && isRateValid,
            listOf(
                isSelfI18NString,
                isTopLeaderI18NString,
                isRateValidI18NString
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return (playerData.playerId == fromId)
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.economyData()
            .taxData.taxRateData.incomeTax.lowIncomeTaxRate = rate
    }
}

/**
 * Change the middle income tax
 *
 * @property rate the new tax rate
 */
@Serializable
data class ChangeMiddleIncomeTaxCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val rate: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Change middle income tax rate to "),
            IntString(0),
            NormalString(". ")
        ),
        listOf(
            rate.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {

        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        }

        val isTopLeader: Boolean = playerData.isTopLeader()
        val isTopLeaderI18NString: I18NString = if (isTopLeader) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotTopLeader(playerData.playerId)
        }

        val isRateValid: Boolean = (rate >= 0.0) && (rate <= 1.0)
        val isRateValidI18NString: I18NString = if (isRateValid) {
            I18NString("")
        } else {
            I18NString("Rate should be between 0 and 1. ")
        }

        return CommandMessage(
            isSelf && isTopLeader && isRateValid,
            listOf(
                isSelfI18NString,
                isTopLeaderI18NString,
                isRateValidI18NString
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return (playerData.playerId == fromId)
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.economyData()
            .taxData.taxRateData.incomeTax.middleIncomeTaxRate = rate
    }
}

/**
 * Change the high income tax
 *
 * @property rate the new tax rate
 */
@Serializable
data class ChangeHighIncomeTaxCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val rate: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Change high income tax rate to "),
            IntString(0),
            NormalString(". ")
        ),
        listOf(
            rate.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {

        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        }

        val isTopLeader: Boolean = playerData.isTopLeader()
        val isTopLeaderI18NString: I18NString = if (isTopLeader) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotTopLeader(playerData.playerId)
        }

        val isRateValid: Boolean = (rate >= 0.0) && (rate <= 1.0)
        val isRateValidI18NString: I18NString = if (isRateValid) {
            I18NString("")
        } else {
            I18NString("Rate should be between 0 and 1. ")
        }

        return CommandMessage(
            isSelf && isTopLeader && isRateValid,
            listOf(
                isSelfI18NString,
                isTopLeaderI18NString,
                isRateValidI18NString
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return (playerData.playerId == fromId)
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.economyData()
            .taxData.taxRateData.incomeTax.highIncomeTaxRate = rate
    }
}

/**
 * Change the boundary between low and middle income
 *
 * @property boundary income higher than this is qualified as middle income
 */
@Serializable
data class ChangeLowMiddleBoundaryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val boundary: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Change the boundary between low and middle income to "),
            IntString(0),
            NormalString(". ")
        ),
        listOf(
            boundary.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {

        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        }

        val isTopLeader: Boolean = playerData.isTopLeader()
        val isTopLeaderI18NString: I18NString = if (isTopLeader) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotTopLeader(playerData.playerId)
        }

        val isBoundaryValid: Boolean = (boundary >= 0.0)
        val isBoundaryValidI18NString: I18NString = if (isBoundaryValid) {
            I18NString("")
        } else {
            I18NString("Boundary should be larger than 0. ")
        }

        return CommandMessage(
            isSelf && isTopLeader && isBoundaryValid,
            listOf(
                isSelfI18NString,
                isTopLeaderI18NString,
                isBoundaryValidI18NString
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return (playerData.playerId == fromId)
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.economyData()
            .taxData.taxRateData.incomeTax.lowMiddleBoundary = boundary
    }
}

/**
 * Change the boundary between middle and high income
 *
 * @property boundary income higher than this is qualified as high income
 */
@Serializable
data class ChangeMiddleHighBoundaryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val boundary: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Change the boundary between middle and high income to "),
            IntString(0),
            NormalString(". ")
        ),
        listOf(
            boundary.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {

        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        }

        val isTopLeader: Boolean = playerData.isTopLeader()
        val isTopLeaderI18NString: I18NString = if (isTopLeader) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotTopLeader(playerData.playerId)
        }

        val isBoundaryValid: Boolean = (boundary >= 0.0)
        val isBoundaryValidI18NString: I18NString = if (isBoundaryValid) {
            I18NString("")
        } else {
            I18NString("Boundary should be larger than 0. ")
        }

        return CommandMessage(
            isSelf && isTopLeader && isBoundaryValid,
            listOf(
                isSelfI18NString,
                isTopLeaderI18NString,
                isBoundaryValidI18NString
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return (playerData.playerId == fromId)
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.economyData()
            .taxData.taxRateData.incomeTax.middleHighBoundary = boundary
    }
}

/**
 * Transfer resource from storage to production
 *
 * @property resourceType the type of the resource to be transferred
 * @property resourceQualityClass the class of the resource to be transferred
 * @property amount the amount of resource to transfer
 */
@Serializable
data class TransferResourceToProductionCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
    val amount: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Transfer "),
            IntString(0),
            NormalString(" of "),
            IntTranslateString(1),
            NormalString(" of class "),
            IntTranslateString(2),
            NormalString(" from storage to production. ")
        ),
        listOf(
            amount.toString(),
            resourceType.toString(),
            resourceQualityClass.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {
        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        }

        val hasStorage: Boolean =
            playerData.playerInternalData.economyData().resourceData.getStorageResourceAmount(
                resourceType,
                resourceQualityClass
            ) >= amount
        val hasStorageI18NString: I18NString = if (hasStorage) {
            I18NString("")
        } else {
            I18NString("Not enough resource in storage. ")
        }

        return CommandMessage(
            isSelf && hasStorage,
            listOf(
                isSelfI18NString,
                hasStorageI18NString,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val isSelf: Boolean = playerData.playerId == fromId

        val hasStorage: Boolean =
            playerData.playerInternalData.economyData().resourceData.getStorageResourceAmount(
                resourceType,
                resourceQualityClass
            ) >= amount

        return isSelf && hasStorage
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val amountData: MutableResourceAmountData =
            playerData.playerInternalData.economyData().resourceData.getResourceAmountData(
                resourceType,
                resourceQualityClass
            )

        amountData.storage -= amount
        amountData.production += amount
    }
}

/**
 * Transfer resource from storage to trade
 *
 * @property resourceType the type of the resource to be transferred
 * @property resourceQualityClass the class of the resource to be transferred
 * @property amount the amount of resource to transfer
 */
@Serializable
data class TransferResourceToTradeCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
    val amount: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Transfer "),
            IntString(0),
            NormalString(" of "),
            IntTranslateString(1),
            NormalString(" of class "),
            IntTranslateString(2),
            NormalString(" from storage to trade. ")
        ),
        listOf(
            amount.toString(),
            resourceType.toString(),
            resourceQualityClass.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {
        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        }

        val hasStorage: Boolean =
            playerData.playerInternalData.economyData().resourceData.getStorageResourceAmount(
                resourceType,
                resourceQualityClass
            ) >= amount
        val hasStorageI18NString: I18NString = if (hasStorage) {
            I18NString("")
        } else {
            I18NString("Not enough resource in storage. ")
        }

        return CommandMessage(
            isSelf && hasStorage,
            listOf(
                isSelfI18NString,
                hasStorageI18NString,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val isSelf: Boolean = playerData.playerId == fromId

        val hasStorage: Boolean =
            playerData.playerInternalData.economyData().resourceData.getStorageResourceAmount(
                resourceType,
                resourceQualityClass
            ) >= amount

        return isSelf && hasStorage
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val amountData: MutableResourceAmountData =
            playerData.playerInternalData.economyData().resourceData.getResourceAmountData(
                resourceType,
                resourceQualityClass
            )

        amountData.storage -= amount
        amountData.trade += amount
    }
}

/**
 * Change the storage resource target amount
 *
 * @property resourceType the type of the resource
 * @property resourceQualityClass the class of the resource
 * @property targetAmount the target amount of the resource
 */
@Serializable
data class ChangeStorageResourceTargetCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
    val targetAmount: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Change the target amount of "),
            IntTranslateString(0),
            NormalString(" of class "),
            IntTranslateString(1),
            NormalString(" for storage to "),
            IntString(2),
            NormalString(". ")
        ),
        listOf(
            resourceType.toString(),
            resourceQualityClass.toString(),
            targetAmount.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {
        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        }

        return CommandMessage(
            isSelf,
            listOf(
                isSelfI18NString,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return playerData.playerId == fromId
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val targetAmountData: MutableResourceAmountData =
            playerData.playerInternalData.economyData().resourceData.getResourceTargetAmountData(
                resourceType,
                resourceQualityClass
            )

        targetAmountData.storage = targetAmount
    }
}

/**
 * Change the production resource target amount
 *
 * @property resourceType the type of the resource
 * @property resourceQualityClass the class of the resource
 * @property targetAmount the target amount of the resource
 */
@Serializable
data class ChangeProductionResourceTargetCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
    val targetAmount: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Change the target amount of "),
            IntTranslateString(0),
            NormalString(" of class "),
            IntTranslateString(1),
            NormalString(" for production to "),
            IntString(2),
            NormalString(". ")
        ),
        listOf(
            resourceType.toString(),
            resourceQualityClass.toString(),
            targetAmount.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {
        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        }

        return CommandMessage(
            isSelf,
            listOf(
                isSelfI18NString,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return playerData.playerId == fromId
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val targetAmountData: MutableResourceAmountData =
            playerData.playerInternalData.economyData().resourceData.getResourceTargetAmountData(
                resourceType,
                resourceQualityClass
            )

        targetAmountData.production = targetAmount
    }
}

/**
 * Change the lower bound of the resource quality of a quality class
 *
 * @property resourceType change the bound of this resource type
 * @property resourceQualityClass change the bound of this class
 * @property lowerBound the new resource quality lower bound
 */
@Serializable
data class ChangeResourceClassBoundCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
    val lowerBound: ResourceQualityData,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Change the lower quality bound of "),
            IntTranslateString(0),
            NormalString(" of class "),
            IntTranslateString(1),
            NormalString(" to "),
            IntString(2),
            NormalString(" (quality1). ")
        ),
        listOf(
            resourceType.toString(),
            resourceQualityClass.toString(),
            lowerBound.quality1.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {
        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        }

        return CommandMessage(
            isSelf,
            listOf(
                isSelfI18NString,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return playerData.playerId == fromId
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.economyData().resourceData.getSingleResourceData(
            resourceType,
            resourceQualityClass
        ).resourceQualityLowerBound = lowerBound.toMutableResourceQualityData()
    }
}

/**
 * Change the salary of pop
 *
 * @property carrierId the id of the carrier where the pop is located
 * @property popType the type of the pop
 * @property salary the new salary
 */
@Serializable
data class ChangeSalaryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val carrierId: Int,
    val popType: PopType,
    val salary: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Change the salary of pop "),
            IntTranslateString(0),
            NormalString(" in carrier "),
            IntString(1),
            NormalString(" to "),
            IntString(2),
            NormalString(". ")
        ),
        listOf(
            popType.toString(),
            carrierId.toString(),
            salary.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {
        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        }

        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId)
        val hasCarrierI18NString: I18NString = if (hasCarrier) {
            I18NString("")
        } else {
            I18NString("Carrier does not exist. ")
        }

        return CommandMessage(
            isSelf && hasCarrier,
            listOf(
                isSelfI18NString,
                hasCarrierI18NString,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val isSelf: Boolean = playerData.playerId == fromId

        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId)

        return isSelf && hasCarrier
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
            carrierId
        ).allPopData.getCommonPopData(popType).salary = salary
    }
}