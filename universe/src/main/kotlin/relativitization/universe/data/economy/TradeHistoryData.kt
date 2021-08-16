package relativitization.universe.data.economy

import kotlinx.serialization.Serializable

/**
 * Store trading history for price adjustment
 */
@Serializable
data class TradeHistoryData(
    val historyMap: Map<Int, Map<ResourceType, Map<ResourceQualityClass, TradeRecordData>>> = mapOf(),
) {
    fun getTradeRecord(
        time: Int,
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): TradeRecordData {
        return historyMap.get(time)?.get(resourceType)?.get(resourceQualityClass) ?: TradeRecordData()
    }
}

@Serializable
data class MutableTradeHistoryData(
    val historyMap: MutableMap<Int, MutableMap<ResourceType, MutableMap<ResourceQualityClass, MutableTradeRecordData>>> = mutableMapOf(),
) {

    fun getTradeRecord(
        time: Int,
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): MutableTradeRecordData {
        return historyMap.get(time)?.get(resourceType)?.get(resourceQualityClass) ?: MutableTradeRecordData()
    }
}

/**
 * Sell in a single turn
 *
 * @property resourceQualityData the quality data of the resource
 * @property tradeAmount the amount of the resource sold
 * @property price the price of the resource in fuel rest mass
 */
@Serializable
data class TradeRecordData(
    val resourceQualityData: ResourceQualityData = ResourceQualityData(),
    val tradeAmount: Double = 0.0,
    val price: Double = 0.0,
)

@Serializable
data class MutableTradeRecordData(
    var resourceQualityData: MutableResourceQualityData = MutableResourceQualityData(),
    var tradeAmount: Double = 0.0,
    var price: Double = 0.0,
) {
    fun addTradeRecord(newData: MutableTradeRecordData) {
        price = (tradeAmount * price + newData.tradeAmount * newData.price) /
                (tradeAmount + newData.tradeAmount)

        resourceQualityData.updateQuality(
            tradeAmount,
            newData.tradeAmount,
            newData.resourceQualityData
        )

        tradeAmount += newData.tradeAmount
    }
}