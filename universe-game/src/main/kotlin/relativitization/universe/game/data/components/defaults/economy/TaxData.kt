package relativitization.universe.game.data.components.defaults.economy

import ksergen.annotations.GenerateImmutable

/**
 * Store tax rate and temporary tax storage
 */
@GenerateImmutable
data class MutableTaxData(
    var taxRateData: MutableTaxRateData = MutableTaxRateData(),
    var storedFuelRestMass: Double = 0.0,
)

/**
 * Tax rate data
 */
@GenerateImmutable
data class MutableTaxRateData(
    var importTariff: MutableTariffData = MutableTariffData(),
    var exportTariff: MutableTariffData = MutableTariffData(),
    var incomeTax: MutableIncomeTaxData = MutableIncomeTaxData(),
)

/**
 * Tariff data
 *
 * @property defaultTariffRate the default tariff data, unless specified in tariffRatePlayerMap
 * @property tariffRatePlayerMap map from top leader id to tariff rate
 */
@GenerateImmutable
data class MutableTariffData(
    var defaultTariffRate: MutableTariffRateData = MutableTariffRateData(),
    var tariffRatePlayerMap: MutableMap<Int, MutableTariffRateData> = mutableMapOf(),
)

fun TariffData.getResourceTariffRate(topLeaderId: Int, resourceType: ResourceType): Double {
    return tariffRatePlayerMap.getOrDefault(topLeaderId, defaultTariffRate)
        .getResourceTariffRate(resourceType)
}

fun MutableTariffData.getResourceTariffRate(topLeaderId: Int, resourceType: ResourceType): Double {
    return tariffRatePlayerMap.getOrDefault(topLeaderId, defaultTariffRate)
        .getResourceTariffRate(resourceType)
}

@GenerateImmutable
data class MutableTariffRateData(
    var resourceTariffRateMap: MutableMap<ResourceType, Double> = mutableMapOf(),
)

fun TariffRateData.getResourceTariffRate(resourceType: ResourceType): Double =
    resourceTariffRateMap.getOrDefault(resourceType, 0.0)

fun MutableTariffRateData.getResourceTariffRate(resourceType: ResourceType): Double =
    resourceTariffRateMap.getOrDefault(resourceType, 0.0)

@GenerateImmutable
data class MutableIncomeTaxData(
    var lowIncomeTaxRate: Double = 0.0,
    var middleIncomeTaxRate: Double = 0.0,
    var highIncomeTaxRate: Double = 0.0,
    var lowMiddleBoundary: Double = 1.0,
    var middleHighBoundary: Double = 2.0,
)

fun IncomeTaxData.getIncomeTax(
    salary: Double
): Double {
    return when {
        salary < lowMiddleBoundary -> lowIncomeTaxRate
        salary < middleHighBoundary -> middleIncomeTaxRate
        else -> highIncomeTaxRate
    }
}

fun MutableIncomeTaxData.getIncomeTax(
    salary: Double
): Double {
    return when {
        salary < lowMiddleBoundary -> lowIncomeTaxRate
        salary < middleHighBoundary -> middleIncomeTaxRate
        else -> highIncomeTaxRate
    }
}
