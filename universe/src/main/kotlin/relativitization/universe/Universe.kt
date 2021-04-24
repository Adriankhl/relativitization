package relativitization.universe

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import relativitization.universe.ai.AI
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData
import relativitization.universe.data.physics.Int3D
import relativitization.universe.data.physics.Int4D
import relativitization.universe.maths.grid.Grids.create3DGrid

class Universe(private val universeData: UniverseData) {
    private val xDim = universeData.universeSettings.xDim
    private val yDim = universeData.universeSettings.yDim
    private val zDim = universeData.universeSettings.zDim

    val int3DList: List<Int3D> = create3DGrid(xDim, yDim, zDim) {
        x, y, z -> Int3D(x, y, z)
    }.flatten().flatten()

    val playerCollection: PlayerCollection = PlayerCollection(xDim, yDim, zDim)

    init {
        // for each player data at the latest time slice, create player object and add to universe3D
        universeData.universeData4D.getLatest().flatten().flatten().flatten().forEach {
            playerCollection.addPlayer(it)
        }
    }

    suspend fun computeAICommands(ai: AI) = coroutineScope {
        val time: Int = universeData.universeState.getCurrentTime()
        val playerId3D: List<List<List<List<Int>>>> = playerCollection.getPlayerId3D()
        int3DList.map { int3D ->
            async(Dispatchers.Default) {
                val viewMap = universeData.toUniverseData3DAtGrid(Int4D(time, int3D)).idToUniverseData3DAtPlayer()
                playerId3D[int3D.x][int3D.y][int3D.z].map { id ->
                    id to ai.compute(viewMap.getValue(id))
                }
            }
        }
    }
}