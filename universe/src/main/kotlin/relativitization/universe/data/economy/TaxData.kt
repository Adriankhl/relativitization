package relativitization.universe.data.economy

import kotlinx.serialization.Serializable

/**
 * Tax rate data
 */
@Serializable
data class TaxRateData(
    val tariffRate: Double = 0.0,
    val minIncomeTaxRate: Double = 0.0,
    val maxIncomeTaxRate: Double = 0.0,
    val minIncomeTaxRateBoundary: Double = 0.0,
    val maxIncomeTaxRateBoundary: Double = 0.0,
)

@Serializable
data class MutableTaxRateData(
    var tariffRate: Double = 0.0,
    var minIncomeTaxRate: Double = 0.0,
    var maxIncomeTaxRate: Double = 0.0,
    var minIncomeTaxRateBoundary: Double = 0.0,
    var maxIncomeTaxRateBoundary: Double = 0.0,
)