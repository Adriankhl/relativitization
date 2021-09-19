package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.component.economy.ResourceQualityClass
import relativitization.universe.data.component.economy.ResourceQualityData
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.RealString
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.pow

/**
 * Send resource (apart from fuel) from yourself to another player
 */
@Serializable
data class SendResourceCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
    val resourceQualityData: ResourceQualityData,
    val amount: Double,
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

    override fun canSend(playerData: MutablePlayerData, universeSettings: UniverseSettings): CanSendWithMessage {
        val notFuel: Boolean = resourceType != ResourceType.FUEL
        val notFuelI18NString: I18NString = if (notFuel) {
            I18NString("")
        } else {
            I18NString("Cannot send fuel as resource. ")
        }

        val hasAmount: Boolean = playerData.playerInternalData.economyData().resourceData.getTradeResourceAmount(
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

        val sameQuality: Boolean = playerData.playerInternalData.economyData().resourceData.getResourceQuality(
            resourceType, resourceQualityClass
        ).toResourceQualityData() == resourceQualityData
        val sameQualityI18NString: I18NString = if (sameQuality) {
            I18NString("")
        } else {
            I18NString(
                listOf(
                    RealString("Resource quality "),
                    IntString(0),
                    RealString(" is not equal to "),
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

        return CanSendWithMessage(
            notFuel && hasAmount && sameQuality,
            I18NString.combine(listOf(notFuelI18NString, hasAmountI18NString, sameQualityI18NString))
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
        val lossFractionPerDistance: Double = playerData.playerInternalData.playerScienceData().playerScienceProductData.resourceLogisticsLossFractionPerDistance
        val distance: Double = Intervals.distance(
            fromInt4D.toDouble3D(),
            playerData.int4D.toDouble3D()
        )

        val lossFraction: Double = if (distance < 1.0) {
            0.0
        } else {
            lossFractionPerDistance.pow(distance)
        }

        playerData.playerInternalData.economyData().resourceData.addNewResource(
            resourceType = resourceType,
            newResourceQuality = resourceQualityData.toMutableResourceQualityData(),
            amount = amount * lossFraction
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
data class SendFuelCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val amount: Double,
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

    override fun canSend(playerData: MutablePlayerData, universeSettings: UniverseSettings): CanSendWithMessage {
        val hasAmount: Boolean = playerData.playerInternalData.physicsData().fuelRestMassData.trade >= amount
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
                    playerData.playerInternalData.physicsData().fuelRestMassData.trade.toString(),
                    amount.toString()
                )
            )
        }

        return CanSendWithMessage(
            hasAmount,
            hasAmountI18NString
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
        val lossFractionPerDistance: Double = playerData.playerInternalData.playerScienceData().playerScienceProductData.fuelLogisticsLossFractionPerDistance
        val distance: Double = Intervals.distance(
            fromInt4D.toDouble3D(),
            playerData.int4D.toDouble3D()
        )

        val lossFraction: Double = if (distance < 1.0) {
            0.0
        } else {
            lossFractionPerDistance.pow(distance)
        }

        playerData.playerInternalData.physicsData().addFuel(amount * lossFraction)
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}