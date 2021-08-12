package relativitization.universe.data.economy

import kotlinx.serialization.Serializable

/**
 * @property taxRateData data about the tax rate of various stuff
 */
@Serializable
data class EconomyData(
    val taxRateData: TaxRateData = TaxRateData(),
    val resourceStockMap: Map<ResourceType, ResourceStockData> = mapOf(),
    val resourceMarketMap: Map<ResourceType, ResourceMarketData> = mapOf(),
)

@Serializable
data class MutableEconomyData(
    var taxRateData: MutableTaxRateData = MutableTaxRateData(),
    var resourceStockMap: MutableMap<ResourceType, MutableResourceStockData> = mutableMapOf(),
    var resourceMarketMap: Map<ResourceType, MutableResourceMarketData> = mutableMapOf(),
)