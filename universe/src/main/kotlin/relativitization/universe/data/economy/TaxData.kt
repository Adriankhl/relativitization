package relativitization.universe.data.economy

import kotlinx.serialization.Serializable
import relativitization.universe.data.economy.resource.ResourceType

/**
 * Tax rate data
 */
@Serializable
data class TaxRateData(
    val tariffRateMap: Map<ResourceType, Double> = mapOf(),
    val minIncomeTaxRate: Double = 0.0,
    val maxIncomeTaxRate: Double = 0.0,
    val minIncomeTaxRateBoundary: Double = 0.0,
    val maxIncomeTaxRateBoundary: Double = 0.0,
) {
    fun getTariff(resourceType: ResourceType): Double = tariffRateMap.getOrDefault(resourceType, 0.0)
}

@Serializable
data class MutableTaxRateData(
    var tariffRateMap: MutableMap<ResourceType, Double> = mutableMapOf(),
    var minIncomeTaxRate: Double = 0.0,
    var maxIncomeTaxRate: Double = 0.0,
    var minIncomeTaxRateBoundary: Double = 0.0,
    var maxIncomeTaxRateBoundary: Double = 0.0,
)