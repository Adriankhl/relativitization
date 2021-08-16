package relativitization.universe.data.economy

import kotlinx.serialization.Serializable

/**
 * Store trading history for price adjustment
 */
@Serializable
data class TradeHistoryData(
    val historyMap: Map<Int, Map<ResourceType, Map<ResourceQualityClass, TradeRecordData>>> = mapOf(),
)

@Serializable
data class MutableTradeHistoryData(
    val historyMap: MutableMap<Int, MutableMap<ResourceType, MutableMap<ResourceQualityClass, MutableTradeRecordData>>> = mutableMapOf(),
)

/**
 * Sell in a single turn
 *
 * @property time the time of the trade
 * @property resourceType the type of resource
 * @property resourceQualityClass the quality class of the resource
 * @property resourceQualityData the quality data of the resource
 * @property tradeAmount the amount of the resource sold
 * @property price the price of the resource in fuel rest mass
 */
@Serializable
data class TradeRecordData(
    val time: Int = -1,
    val resourceType: ResourceType = ResourceType.PLANT,
    val resourceQualityClass: ResourceQualityClass = ResourceQualityClass.FIRST,
    val resourceQualityData: ResourceQualityData = ResourceQualityData(),
    val tradeAmount: Double = 0.0,
    val price: Double = 0.0,
)

@Serializable
data class MutableTradeRecordData(
    var time: Int = -1,
    var resourceType: ResourceType = ResourceType.PLANT,
    var resourceQualityClass: ResourceQualityClass = ResourceQualityClass.FIRST,
    var resourceQualityData: MutableResourceQualityData = MutableResourceQualityData(),
    var tradeAmount: Double = 0.0,
    var price: Double = 0.0,
) {
    fun addSellRecord(mutableTradeRecordData: MutableTradeRecordData) {

    }
}