package relativitization.universe

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import org.apache.logging.log4j.LogManager
import relativitization.universe.ai.AI
import relativitization.universe.ai.PickAI
import relativitization.universe.data.*
import relativitization.universe.data.commands.Command
import relativitization.universe.data.physics.Int3D
import relativitization.universe.data.physics.Int4D
import relativitization.universe.data.serializer.DataSerializer.encode
import relativitization.universe.data.serializer.DataSerializer.decode
import relativitization.universe.maths.grid.Grids.create3DGrid
import relativitization.universe.utils.CoroutineBoolean
import relativitization.universe.utils.CoroutineMap

import java.io.File

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

    private val int3DList: List<Int3D> = create3DGrid(xDim, yDim, zDim) {
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
    suspend fun computeAICommands() = coroutineScope {
        val time: Int = universeData.universeState.getCurrentTime()
        val playerId3D: List<List<List<List<Int>>>> = playerCollection.getPlayerId3D()
        int3DList.map { int3D ->
            async(Dispatchers.Default) {
                val viewMap = universeData.toUniverseData3DAtGrid(Int4D(time, int3D)).idToUniverseData3DAtPlayer()
                playerId3D[int3D.x][int3D.y][int3D.z].map { id ->
                    id to PickAI.compute(viewMap.getValue(id))
                }
            }
        }
    }

    /**
     * Save Latest universe data
     */
    fun saveLatest() {
        val saveDir = "./saves/${universeData.universeSettings.universeName}"
        val latestTime: Int = universeData.universeState.getCurrentTime()

        // Make Directory
        File("$saveDir").mkdirs()

        // save universe 4D slice
        File("${saveDir}/universeData4DSlice-${latestTime}.json").writeText(
            encode(universeData.universeData4D.getLatest())
        )

        // save state
        File("${saveDir}/universeState-${latestTime}.json").writeText(
            encode(universeData.universeState)
        )

        // save settings, setting should be immutable, so only one save is enough
        File("${saveDir}/universeSetting.json").writeText(
            encode(universeData.universeSettings)
        )

        // save commands
        File("${saveDir}/commandMap-${latestTime}.json").writeText(
            encode(universeData.commandMap)
        )

        // Additionally save state to latestState.json for loading
        File("${saveDir}/latestState.json").writeText(
            encode(universeData.universeState)
        )
    }

    /**
     * Save the whole universe
     */
    fun saveAll() {
        val saveDir = "saves/${universeData.universeSettings.universeName}"
        val latestTime: Int = universeData.universeState.getCurrentTime()
        val oldestTime: Int = latestTime - universeData.universeSettings.tDim + 1
        val oldUniverseData4D =  universeData.universeData4D.getAllExcludeLatest()

        File("$saveDir").mkdirs()

        for (i in oldUniverseData4D.indices) {
            File("${saveDir}/universeData4DSlice-${oldestTime + i}.json").writeText(
                encode(oldUniverseData4D[i])
            )
        }

        // save settings, setting should be immutable, so only one save is enough
        File("${saveDir}/universeSetting.json").writeText(
            encode(universeData.universeSettings)
        )

        // Also save the latest slice, setting, state, commandMap
        saveLatest()
    }

    /**
     * Prepare universe after the beginning of the turn
     */
    fun prepareUniverse() {
        val time: Int = universeData.universeState.getCurrentTime()
        val playerId3D: List<List<List<List<Int>>>> = playerCollection.getPlayerId3D()
    }

    companion object {
        private val logger = LogManager.getLogger()

        fun loadUniverseLatest(universeName: String): UniverseData {
            val saveDir = "saves/$universeName"

            // save settings, setting should be immutable, so only one save is enough
            val universeSettings: UniverseSettings = decode(File("${saveDir}/universeSetting.json").readText())

            // load latest universe state
            val universeState: UniverseState = decode(File("${saveDir}/latestState.json").readText())

            val latestTime: Int = universeState.getCurrentTime()
            val oldestTime: Int = latestTime - universeSettings.tDim + 1

            val commandMap: MutableMap<Int, MutableList<Command>> = decode(
                File("${saveDir}/commandMap-${latestTime}.json").readText()
            )

            val playerData4D: MutableList<List<List<List<List<PlayerData>>>>> = mutableListOf()
            for (time in oldestTime..latestTime) {
                playerData4D.add(decode(File("${saveDir}/universeData4DSlice-${time}.json").readText()))
            }

            val universeData4D = UniverseData4D(playerData4D)

            return UniverseData(
                universeData4D,
                universeSettings,
                universeState,
                commandMap
            )

        }
    }

}