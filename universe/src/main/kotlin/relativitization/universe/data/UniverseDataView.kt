package relativitization.universe.data

import kotlinx.serialization.Serializable
import relativitization.universe.data.physics.Int3D
import relativitization.universe.data.physics.Int4D

@Serializable
data class UniverseData3DAtGrid(
    val center: Int4D,
    val centerPlayerDataList: List<PlayerData>,
    val playerDataMap: Map<Int, Pair<Int3D, PlayerData>>,
    val playerId3D: List<List<List<List<Int>>>>,
    val xDim: Int,
    val yDim: Int,
    val zDim: Int,
)