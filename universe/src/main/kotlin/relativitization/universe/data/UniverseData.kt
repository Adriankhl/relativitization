package relativitization.universe.data

import kotlinx.serialization.Serializable

@Serializable
data class UniverseData(
    val speedOfLight: Int = 1,
)