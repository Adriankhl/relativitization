package relativitization.universe.data

import kotlinx.serialization.Serializable

@Serializable
data class UniverseSettings(
    val universeName: String,
    val speedOfLight: Int,
    val tDim: Int,
    val xDim: Int,
    val yDim: Int,
    val zDim: Int,
)


@Serializable
data class MutableUniverseSettings(
    var universeName: String = "Test",
    var speedOfLight: Int = 1,
    var tDim: Int = 8,
    var xDim: Int = 2,
    var yDim: Int = 2,
    var zDim: Int = 2,
)