package relativitization.universe.data.component.economy

import kotlinx.serialization.Serializable

/**
 * Store tax rate and temporary tax storage
 */
@Serializable
data class TaxData(
    val taxRateData: TaxRateData = TaxRateData(),
    val storedFuelRestMass: Double = 0.0,
)

@Serializable
data class MutableTaxData(
    var taxRateData: MutableTaxRateData = MutableTaxRateData(),
    var storedFuelRestMass: Double = 0.0,
)

/**
 * Tax rate data
 */
@Serializable
data class TaxRateData(
    val importTariff: TariffData = TariffData(),
    val exportTariff: TariffData = TariffData(),
    val incomeTax: IncomeTaxData = IncomeTaxData(),
)

@Serializable
data class MutableTaxRateData(
    var importTariff: MutableTariffData = MutableTariffData(),
    var exportTariff: MutableTariffData = MutableTariffData(),
    var incomeTax: MutableIncomeTaxData = MutableIncomeTaxData(),
)

@Serializable
data class TariffData(
    val defaultTariffRate: TariffRateData = TariffRateData(),
    val tariffRatePlayerMap: Map<Int, TariffRateData> = mapOf(),
) {
    fun getTariffRate(playerId: Int, resourceType: ResourceType): Double {
        return tariffRatePlayerMap.getOrDefault(playerId, defaultTariffRate).getTariffRate(resourceType)
    }
}

@Serializable
data class MutableTariffData(
    var defaultTariffRate: MutableTariffRateData = MutableTariffRateData(),
    var tariffRatePlayerMap: MutableMap<Int, MutableTariffRateData> = mutableMapOf(),
) {
    fun getTariffRate(playerId: Int, resourceType: ResourceType): Double {
        return tariffRatePlayerMap.getOrDefault(playerId, defaultTariffRate).getTariffRate(resourceType)
    }
}

@Serializable
data class TariffRateData(
    val tariffRateMap: Map<ResourceType, Double> = mapOf()
) {
    fun getTariffRate(resourceType: ResourceType): Double = tariffRateMap.getOrDefault(resourceType, 0.0)
}

@Serializable
data class MutableTariffRateData(
    var tariffRateMap: MutableMap<ResourceType, Double> = mutableMapOf()
) {
    fun getTariffRate(resourceType: ResourceType): Double = tariffRateMap.getOrDefault(resourceType, 0.0)
}

@Serializable
data class IncomeTaxData(
    val lowIncomeTaxRate: Double = 0.0,
    val middleIncomeTaxRate: Double = 0.0,
    val highIncomeTaxRate: Double = 0.0,
    val lowMiddleBoundary: Double = 1.0,
    val middleHighBoundary: Double = 2.0,
)

@Serializable
data class MutableIncomeTaxData(
    var lowIncomeTaxRate: Double = 0.0,
    var middleIncomeTaxRate: Double = 0.0,
    var highIncomeTaxRate: Double = 0.0,
    var lowMiddleBoundary: Double = 1.0,
    var middleHighBoundary: Double = 2.0,
)