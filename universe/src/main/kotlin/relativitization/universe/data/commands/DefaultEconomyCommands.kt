package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.defaults.economy.MutableResourceAmountData
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.economy.ResourceTargetProportionData
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.components.economyData
import relativitization.universe.data.serializer.DataSerializer
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
    override fun description(): I18NString = I18NString(
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
    ): CommandErrorMessage {

        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        )

        val isTopLeader = CommandErrorMessage(
            playerData.isTopLeader(),
            CommandI18NStringFactory.isNotTopLeader(playerData.playerId)
        )

        val isRateValid = CommandErrorMessage(
            (rate >= 0.0),
            I18NString("Rate should be greater than 0.  ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                isTopLeader,
                isRateValid,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf
            )
        )
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
    override fun description(): I18NString = I18NString(
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
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        )

        val isTopLeader = CommandErrorMessage(
            playerData.isTopLeader(),
            CommandI18NStringFactory.isNotTopLeader(playerData.playerId)
        )

        val isRateValid = CommandErrorMessage(
            (rate >= 0.0),
            I18NString("Rate should be greater than 0.  ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                isTopLeader,
                isRateValid,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf
            )
        )
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
    override fun description(): I18NString = I18NString(
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
    ): CommandErrorMessage {

        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        )

        val isTopLeader = CommandErrorMessage(
            playerData.isTopLeader(),
            CommandI18NStringFactory.isNotTopLeader(playerData.playerId)
        )

        val isRateValid = CommandErrorMessage(
            (rate >= 0.0) && (rate <= 1.0),
            I18NString("Rate should be between 0 and 1. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                isTopLeader,
                isRateValid,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf
            )
        )
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
    override fun description(): I18NString = I18NString(
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
    ): CommandErrorMessage {

        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        )

        val isTopLeader = CommandErrorMessage(
            playerData.isTopLeader(),
            CommandI18NStringFactory.isNotTopLeader(playerData.playerId)
        )

        val isRateValid = CommandErrorMessage(
            (rate >= 0.0) && (rate <= 1.0),
            I18NString("Rate should be between 0 and 1. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                isTopLeader,
                isRateValid,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf
            )
        )
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
    override fun description(): I18NString = I18NString(
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
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        )

        val isTopLeader = CommandErrorMessage(
            playerData.isTopLeader(),
            CommandI18NStringFactory.isNotTopLeader(playerData.playerId)
        )

        val isRateValid = CommandErrorMessage(
            (rate >= 0.0) && (rate <= 1.0),
            I18NString("Rate should be between 0 and 1. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                isTopLeader,
                isRateValid,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf
            )
        )
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
    override fun description(): I18NString = I18NString(
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
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        )

        val isTopLeader = CommandErrorMessage(
            playerData.isTopLeader(),
            CommandI18NStringFactory.isNotTopLeader(playerData.playerId)
        )

        val isBoundaryValid = CommandErrorMessage(
            (boundary >= 0.0),
            I18NString("Boundary should be larger than 0. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                isTopLeader,
                isBoundaryValid,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf
            )
        )
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
    override fun description(): I18NString = I18NString(
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
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        )

        val isTopLeader = CommandErrorMessage(
            playerData.isTopLeader(),
            CommandI18NStringFactory.isNotTopLeader(playerData.playerId)
        )

        val lowMiddleBoundary: Double = playerData.playerInternalData.economyData()
            .taxData.taxRateData.incomeTax.lowMiddleBoundary
        val isBoundaryValid = CommandErrorMessage(
            (boundary >= 0.0) && (boundary >= lowMiddleBoundary),
            I18NString("Boundary should be larger than 0 and low-middle boundary. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                isTopLeader,
                isBoundaryValid,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.economyData()
            .taxData.taxRateData.incomeTax.middleHighBoundary = boundary
    }
}

/**
 * Change the storage resource target amount
 *
 * @property resourceType the type of the resource
 * @property resourceQualityClass the class of the resource
 * @property resourceTargetProportionData the target proportion of the resource categories
 */
@Serializable
data class ChangeResourceTargetProportionCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
    val resourceTargetProportionData: ResourceTargetProportionData,
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Change the target proportion of "),
            IntTranslateString(0),
            NormalString(" of class "),
            IntTranslateString(1),
            NormalString(", storage: "),
            IntString(2),
            NormalString(", production: "),
            IntString(2),
            NormalString(", trade: "),
            IntString(3),
            NormalString(". ")
        ),
        listOf(
            resourceType.toString(),
            resourceQualityClass.toString(),
            resourceTargetProportionData.storage.toString(),
            resourceTargetProportionData.production.toString(),
            resourceTargetProportionData.trade.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.economyData().resourceData.getSingleResourceData(
                resourceType,
                resourceQualityClass
        ).resourceTargetProportion = DataSerializer.copy(resourceTargetProportionData)
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
    override fun description(): I18NString = I18NString(
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
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        )

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId),
            I18NString("Carrier does not exist. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId),
            I18NString("Carrier does not exist. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
            carrierId
        ).allPopData.getCommonPopData(popType).salaryPerEmployee = salary
    }
}