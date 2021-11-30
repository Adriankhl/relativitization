package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.RealString

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
            RealString("Change default import tariff rate of $resourceType to "),
            IntString(1),
            RealString(". ")
        ),
        listOf(
            rate.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {

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

        return CanSendCheckMessage(
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
            RealString("Change default export tariff rate of $resourceType to "),
            IntString(0),
            RealString(". ")
        ),
        listOf(
            rate.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {

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

        return CanSendCheckMessage(
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
            RealString("Change low income tax rate to "),
            IntString(0),
            RealString(". ")
        ),
        listOf(
            rate.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {

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

        return CanSendCheckMessage(
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
            RealString("Change middle income tax rate to "),
            IntString(0),
            RealString(". ")
        ),
        listOf(
            rate.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {

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

        return CanSendCheckMessage(
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
            RealString("Change high income tax rate to "),
            IntString(0),
            RealString(". ")
        ),
        listOf(
            rate.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {

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

        return CanSendCheckMessage(
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
            RealString("Change the boundary between low and middle income to "),
            IntString(0),
            RealString(". ")
        ),
        listOf(
            boundary.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {

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

        return CanSendCheckMessage(
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
            RealString("Change the boundary between middle and high income to "),
            IntString(0),
            RealString(". ")
        ),
        listOf(
            boundary.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {

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

        return CanSendCheckMessage(
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

@Serializable
data class TransferResourceToProductionCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D
) : DefaultCommand() {
    override val description: I18NString
        get() = TODO("Not yet implemented")

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        TODO("Not yet implemented")
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        TODO("Not yet implemented")
    }
}