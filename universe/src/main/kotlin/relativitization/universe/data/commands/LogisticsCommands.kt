package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.component.economy.MutableResourceQualityData
import relativitization.universe.data.component.economy.ResourceQualityClass
import relativitization.universe.data.component.economy.ResourceQualityData
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.RealString
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
    val amount: Double,
) : Command() {

    private var resourceQualityData: ResourceQualityData = ResourceQualityData()

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

    override fun canSend(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        val isNotFuel: Boolean = resourceType != ResourceType.FUEL
        val tradeAmount: Double = playerData.playerInternalData.economyData().resourceData.getTradeResourceAmount(
            resourceType, resourceQualityClass
        )
        return isNotFuel && (tradeAmount >= amount)
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
        resourceQualityData = playerData.playerInternalData.economyData().resourceData.getResourceQuality(
            resourceType, resourceQualityClass
        ).toResourceQualityData()
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
}