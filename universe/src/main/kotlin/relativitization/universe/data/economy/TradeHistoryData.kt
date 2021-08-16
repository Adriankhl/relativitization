package relativitization.universe.data.economy

import kotlinx.serialization.Serializable

/**
 * Store trading history for price adjustment
 */
@Serializable
data class TradeHistoryData(
    val historyMap: Map<Int, Map<ResourceType, Map<ResourceQualityClass, SellRecordData>>> = mapOf(),
)

@Serializable
data class MutableTradeHistoryData(
    val historyMap: MutableMap<Int, MutableMap<ResourceType, MutableMap<ResourceQualityClass, MutableSellRecordData>>> = mutableMapOf(),
)

/**
 * Sell in a single turn
 *
 * @property time the time of the trade
 * @property resourceType the type of resource
 * @property soldAmount the amount of the resource sold
 * @property fuelRestMassPrice the price of the resource
 */
@Serializable
data class SellRecordData(
    val time: Int = -1,
    val resourceType: ResourceType = ResourceType.PLANT,
    val soldAmount: Double = 0.0,
    val fuelRestMassPrice: Double = 0.0,
)

@Serializable
data class MutableSellRecordData(
    var time: Int = -1,
    var resourceType: ResourceType = ResourceType.PLANT,
    var soldAmount: Double = 0.0,
    var fuelRestMassPrice: Double = 0.0,
)