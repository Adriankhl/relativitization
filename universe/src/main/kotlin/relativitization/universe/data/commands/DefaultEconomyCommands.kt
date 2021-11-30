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
            RealString("Change default import tariff rate of "),
            IntString(0),
            RealString(" to "),
            IntString(1),
            RealString(". ")
        ),
        listOf(
            resourceType.toString(),
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
            RealString("Change default export tariff rate of "),
            IntString(0),
            RealString(" to "),
            IntString(1),
            RealString(". ")
        ),
        listOf(
            resourceType.toString(),
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