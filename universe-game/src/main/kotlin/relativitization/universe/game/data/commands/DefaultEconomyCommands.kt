package relativitization.universe.game.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.CommandErrorMessage
import relativitization.universe.core.data.commands.CommandI18NStringFactory
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.core.maths.physics.Int4D
import relativitization.universe.core.utils.I18NString
import relativitization.universe.core.utils.IntString
import relativitization.universe.core.utils.IntTranslateString
import relativitization.universe.core.utils.NormalString
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.game.data.components.defaults.economy.ResourceTargetProportionData
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.economy.getSingleResourceData
import relativitization.universe.game.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.game.data.components.defaults.popsystem.pop.getCommonPopData
import relativitization.universe.game.data.components.economyData
import relativitization.universe.game.data.components.popSystemData

/**
 * Change import tariff rate of self
 *
 * @property resourceType the tariff of this resource
 * @property rate the new tariff rate
 */
@Serializable
data class ChangeDefaultImportTariffCommand(
    override val toId: Int,
    val resourceType: ResourceType,
    val rate: Double,
) : DefaultCommand() {
    override fun name(): String = "Change Default Import Tariff"

    override fun description(fromId: Int): I18NString = I18NString(
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
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
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
        fromId: Int,
        fromInt4D: Int4D,
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

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
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
    val resourceType: ResourceType,
    val rate: Double,
) : DefaultCommand() {
    override fun name(): String = "Change Default Export Tariff"

    override fun description(fromId: Int): I18NString = I18NString(
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
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
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
        fromId: Int,
        fromInt4D: Int4D,
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

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
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
    val rate: Double,
) : DefaultCommand() {
    override fun name(): String = "Change Low Income Tax"

    override fun description(fromId: Int): I18NString = I18NString(
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
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
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
        fromId: Int,
        fromInt4D: Int4D,
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

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
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
    val rate: Double,
) : DefaultCommand() {
    override fun name(): String = "Change Middle Income Tax"

    override fun description(fromId: Int): I18NString = I18NString(
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
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
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
        fromId: Int,
        fromInt4D: Int4D,
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

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
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
    val rate: Double,
) : DefaultCommand() {
    override fun name(): String = "Change High Income Tax"

    override fun description(fromId: Int): I18NString = I18NString(
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
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
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
        fromId: Int,
        fromInt4D: Int4D,
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

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
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
    val boundary: Double,
) : DefaultCommand() {
    override fun name(): String = "Change Low-Middle Boundary"

    override fun description(fromId: Int): I18NString = I18NString(
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
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
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
        fromId: Int,
        fromInt4D: Int4D,
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

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
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
    val boundary: Double,
) : DefaultCommand() {
    override fun name(): String = "Change Middle-High Boundary"

    override fun description(fromId: Int): I18NString = I18NString(
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
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
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
        fromId: Int,
        fromInt4D: Int4D,
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

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
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
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
    val resourceTargetProportionData: ResourceTargetProportionData,
) : DefaultCommand() {
    override fun name(): String = "Change Resource Target"

    override fun description(fromId: Int): I18NString = I18NString(
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
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
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

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.economyData().resourceData.getSingleResourceData(
                resourceType,
                resourceQualityClass
        ).resourceTargetProportion = DataSerializer.copy(resourceTargetProportionData)
    }
}

/**
 * Change the base salary of all pop system
 *
 * @property baseSalaryPerEmployee the new base salary per employee
 */
@Serializable
data class ChangeBaseSalaryCommand(
    override val toId: Int,
    val baseSalaryPerEmployee: Double,
) : DefaultCommand() {
    override fun name(): String = "Change Base Salary"

    override fun description(fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Change the base salary to "),
            IntTranslateString(0),
            NormalString(". ")
        ),
        listOf(
            baseSalaryPerEmployee.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
        )

        val isBaseSalaryValid = CommandErrorMessage(
            baseSalaryPerEmployee >= 0.0,
            I18NString("Base salary is not valid")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                isBaseSalaryValid,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
            )
        )
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.popSystemData().generalPopSystemData
            .baseSalaryPerEmployee = baseSalaryPerEmployee
    }
}

/**
 * Change the salary factor of a pop in a specific carrier
 *
 * @property carrierId the id of the carrier where the pop is located
 * @property popType the type of the pop
 * @property salaryFactor the new salary factor
 */
@Serializable
data class ChangeSalaryFactorCommand(
    override val toId: Int,
    val carrierId: Int,
    val popType: PopType,
    val salaryFactor: Double,
) : DefaultCommand() {
    override fun name(): String = "Change Salary Factor"

    override fun description(fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Change the salary factor of pop "),
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
            salaryFactor.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
        )

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId),
            I18NString("Carrier does not exist. ")
        )

        val isSalaryFactorValid = CommandErrorMessage(
            (salaryFactor >= 1.0) && (salaryFactor <= 10.0),
            I18NString("Salary factor is not valid")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                isSalaryFactorValid,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
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

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
            carrierId
        ).allPopData.getCommonPopData(popType).salaryFactor = salaryFactor
    }
}