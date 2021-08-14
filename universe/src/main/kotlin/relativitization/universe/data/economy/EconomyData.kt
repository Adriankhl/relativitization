package relativitization.universe.data.economy

import kotlinx.serialization.Serializable

/**
 * @property taxRateData data about the tax rate of various stuff
 */
@Serializable
data class EconomyData(
    val taxRateData: TaxRateData = TaxRateData(),
    val resourceStockMap: Map<ResourceType, ResourceStockData> = mapOf(),
    val resourcePriceMap: Map<ResourceType, ResourcePriceData> = mapOf(),
    val tradeHistoryData: TradeHistoryData = TradeHistoryData(),
)

@Serializable
data class MutableEconomyData(
    var taxRateData: MutableTaxRateData = MutableTaxRateData(),
    var resourceStockMap: MutableMap<ResourceType, MutableResourceStockData> = mutableMapOf(),
    var resourcePriceMap: Map<ResourceType, MutableResourcePriceData> = mutableMapOf(),
    var tradeHistoryData: MutableTradeHistoryData = MutableTradeHistoryData(),
)