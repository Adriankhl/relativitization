package relativitization.universe.data.science.product

import kotlinx.serialization.Serializable

@Serializable
data class ScienceProductData(
    val maxShipRestMass: Double = 10000.0,
    val maxShipEnginePowerByRestMass: Double = 1E-6,
)