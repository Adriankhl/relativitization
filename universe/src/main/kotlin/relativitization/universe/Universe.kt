package relativitization.universe

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import org.apache.logging.log4j.LogManager
import relativitization.universe.ai.AI
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.data.physics.Int3D
import relativitization.universe.data.physics.Int4D
import relativitization.universe.maths.grid.Grids.create3DGrid
import relativitization.universe.utils.CoroutineBoolean
import relativitization.universe.utils.CoroutineMap

class Universe(private val universeData: UniverseData) {

    private val xDim = universeData.universeSettings.xDim
    private val yDim = universeData.universeSettings.yDim
    private val zDim = universeData.universeSettings.zDim

    // Store all commands from human
    val humanCommandMap: CoroutineMap<Int, List<Command>> = CoroutineMap()

    // Whether this can be accessed (like push to humanCommandMap) by external input (e.g. other human's client)
    // Should be true only after the new universe data is ready and before for all player inputs are done
    // Or maybe before the wait time limit has reached
    val canAccess: CoroutineBoolean = CoroutineBoolean(false)

    val int3DList: List<Int3D> = create3DGrid(xDim, yDim, zDim) {
        x, y, z -> Int3D(x, y, z)
    }.flatten().flatten()

    private val playerCollection: PlayerCollection = PlayerCollection(xDim, yDim, zDim)

    init {
        // for each player data at the latest time slice, create player object and add to universe3D
        universeData.getLatestPlayerDataList().forEach { playerCollection.addPlayer(it) }
    }

    /**
     * True if player is alive
     * Should only return if canAccess is true
     */
    suspend fun isAlive(id: Int): Boolean {
        return playerCollection.hasPlayer(id)
    }

    /**
     * Get all available player
     * Should only return if canAccess is true
     */
    suspend fun availablePlayers(): List<Int> {
        return playerCollection.getAllId()
    }

    /**
     * Get universe 3d view for (human) player
     * Should only return if canAccess is true
     */
    suspend fun getUniverse3DViewAtPlayer(id: Int): UniverseData3DAtPlayer {
        val int4D: Int4D = playerCollection.getPlayerInt4D(id)
        return universeData.toUniverseData3DAtGrid(int4D).idToUniverseData3DAtPlayer().getValue(id)
    }

    /**
     * Compute commands by ai for all player in a coroutine
     */
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

    companion object {
        private val logger = LogManager.getLogger()
    }

}