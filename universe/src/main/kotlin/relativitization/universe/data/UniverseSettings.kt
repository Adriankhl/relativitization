package relativitization.universe.data

import kotlinx.serialization.Serializable

@Serializable
data class UniverseSettings(
    val universeName: String,
    val speedOfLight: Int,
    val numExtraTurnStore: Int,
)


@Serializable
data class MutableUniverseSettings(
    var universeName: String = "Test",
    var speedOfLight: Int = 1,
    var numExtraTurnStore: Int = 0,
)