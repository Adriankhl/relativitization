package relativitization.universe.data.economy

import kotlinx.serialization.Serializable

/**
 * Store trading history for price adjustment
 */
@Serializable
data class TradeHistoryData(
    val firstClassHistoryMap: Map<Int, Map<ResourceType, SellRecordData>> = mapOf(),
    val secondClassHistoryMap: Map<Int, Map<ResourceType, SellRecordData>> = mapOf(),
    val thirdClassHistoryMap: Map<Int, Map<ResourceType, SellRecordData>> = mapOf(),
)

@Serializable
data class MutableTradeHistoryData(
    val firstClassHistoryMap: MutableMap<Int, Map<ResourceType, MutableSellRecordData>> = mutableMapOf(),
    val secondClassHistoryMap: MutableMap<Int, Map<ResourceType, MutableSellRecordData>> = mutableMapOf(),
    val thirdClassHistoryMap: MutableMap<Int, Map<ResourceType, MutableSellRecordData>> = mutableMapOf(),
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