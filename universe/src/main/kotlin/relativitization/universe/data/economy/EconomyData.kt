package relativitization.universe.data.economy

import kotlinx.serialization.Serializable

/**
 * @property taxRateData data about the tax rate of various stuff
 */
@Serializable
data class EconomyData(
    val taxRateData: TaxRateData = TaxRateData(),
)

@Serializable
data class MutableEconomyData(
    var taxRateData: MutableTaxRateData = MutableTaxRateData(),
)