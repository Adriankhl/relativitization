package relativitization.universe.data

import kotlinx.serialization.Serializable
import relativitization.universe.data.physics.Int4D

@Serializable
data class UniverseData3DAtGrid(
    val center: Int4D,
    val playerDataMap: Map<Int, PlayerData>,
    val playerId3D: List<List<List<List<PlayerData>>>>,
    val xDim: Int,
    val yDim: Int,
    val zDim: Int,
)