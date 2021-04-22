package relativitization.universe.data.economy

import kotlinx.serialization.Serializable

/**
 * @property taxRateMap map from type of population to tax rate
 */
@Serializable
data class EconomyData(
    val taxRateMap: Map<String, Double> = mapOf()
)

@Serializable
data class MutableEconomyData(
    var taxRateMap: MutableMap<String, Double> = mutableMapOf()
)