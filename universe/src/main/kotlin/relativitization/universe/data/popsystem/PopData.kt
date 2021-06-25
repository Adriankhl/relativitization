package relativitization.universe.data.popsystem

import kotlinx.serialization.Serializable

@Serializable
data class CommonPopData(
    val population: Double = 100.0,
)