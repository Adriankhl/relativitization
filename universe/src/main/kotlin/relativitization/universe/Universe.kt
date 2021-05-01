package relativitization.universe

import kotlinx.coroutines.*
import org.apache.logging.log4j.LogManager
import relativitization.universe.ai.PickAI
import relativitization.universe.data.*
import relativitization.universe.data.commands.Command
import relativitization.universe.data.physics.Int3D
import relativitization.universe.data.physics.Int4D
import relativitization.universe.data.serializer.DataSerializer.encode
import relativitization.universe.data.serializer.DataSerializer.decode
import relativitization.universe.maths.grid.Grids.create3DGrid
import relativitization.universe.maths.physics.Intervals.intDistance
import relativitization.universe.mechanisms.MechanismCollection
import relativitization.universe.utils.CoroutineBoolean
import relativitization.universe.utils.CoroutineMap
import relativitization.universe.utils.pmap

import java.io.File

/**
 * Main class representing the 4D universe
 * Main stepping function: preProcessUniverse and postProcessUniverse
 */
class Universe(private val universeData: UniverseData) {

    private val xDim = universeData.universeSettings.xDim
    private val yDim = universeData.universeSettings.yDim
    private val zDim = universeData.universeSettings.zDim

    // For iteration
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
     * Run all the mechanism, add generated commands to commandMap
     */
    private suspend fun processMechanism() {
        val time: Int = universeData.universeState.getCurrentTime()
        val playerId3D: List<List<List<List<Int>>>> = playerCollection.getPlayerId3D()

        // Mechanism process, execute produced commands on attached group, and return remaining command
        val commandList: List<Command> = int3DList.pmap { int3D ->
            val viewMap = universeData.toUniverseData3DAtGrid(Int4D(time, int3D)).idToUniverseData3DAtPlayer()

            val playerIdAtGrid: List<Int> = playerId3D[int3D.x][int3D.y][int3D.z]

            val commandListAtGrid: List<Command> = playerIdAtGrid.map { id ->
                val universeData3DAtPlayer = viewMap.getValue(id)

                val commandListFromPlayer: List<Command> = MechanismCollection.mechanismList.map { mechanism ->
                    mechanism.process(playerCollection.getPlayer(id), universeData3DAtPlayer)
                }.flatten()

                commandListFromPlayer
            }.flatten()

            // Differentiate the commands the should be executed immediately, e.g., self and attached player
            // or commands to be saved to command Map
            // In principle, this shouldn't contain self commands since they should be integrated in the mechanism process
            val (commandExecuteList, commandStoreList) = commandListAtGrid.partition {
                val inGrid: Boolean = playerIdAtGrid.contains(it.toId)
                val sameAttached: Boolean = (playerCollection.getPlayer(it.fromId).attachedPlayerId ==
                        playerCollection.getPlayer(it.toId).attachedPlayerId)
                inGrid && sameAttached
            }

            // Check and execute immediate command
            for (command in commandExecuteList) {
                command.checkAndExecute(playerCollection.getPlayer(command.toId), universeData.universeSettings)
            }

            commandStoreList
        }.flatten()

        addToCommandMap(universeData.commandMap, commandList)
    }

    /**
     * Execute commands in parallel
     */
    private suspend fun processCommandMap() = coroutineScope{
        // Remove non existing player from the command map
        val noIdList: List<Int> = universeData.commandMap.keys.filter { !playerCollection.hasPlayer(it) }

        for (id in noIdList) {
            universeData.commandMap.remove(id)
        }

        universeData.commandMap.pmap { id, commandList ->
            val playerInt4D: Int4D = playerCollection.getPlayerInt4D(id)

            // Determine the command to be executed by spacetime distance
            val commandExecuteList: List<Command> = commandList.filter {
                val distance: Int = intDistance(it.fromInt4D, playerInt4D)
                val timeDiff: Int = playerInt4D.t - it.fromInt4D.t
                distance - timeDiff * universeData.universeSettings.speedOfLight <= 0
            }

            // Remove the command to be executed
            commandList.removeAll(commandExecuteList)

            // Check and execute command
            for (command in commandExecuteList) {
                command.checkAndExecute(playerCollection.getPlayer(command.toId), universeData.universeSettings)
            }
        }
    }

    /**
     * Update new players and dead players
     */
    private fun processDeadAndNewPlayer() {
        playerCollection.cleanDeadPlayer()
        playerCollection.addNewPlayerFromPlayerData(universeData.universeState)
    }

    /**
     * Process human and ai command input
     *
     * @param humanInputCommands map from player id to the command list from this player
     * @param aiInputCommands map from player id to the command list computed by ai
     */
    private suspend fun processCommandInput(
        humanInputCommands: Map<Int, List<Command>>,
        aiInputCommands: Map<Int, List<Command>>
    ) {
        // Add two input command map, prefer human input commands
        val inputCommands: Map<Int, List<Command>> = aiInputCommands.filter {
                (id, _) -> !humanInputCommands.containsKey(id)
        } + humanInputCommands

        // Default all player type to Ai
        for ((id, _) in aiInputCommands) {
            if (playerCollection.getPlayer(id).playerType != PlayerType.NONE ) {
                playerCollection.getPlayer(id).playerType = PlayerType.AI
            }
        }

        // Then change the player type to human if there is human input
        for ((id, _) in humanInputCommands) {
            if (playerCollection.getPlayer(id).playerType != PlayerType.NONE ) {
                playerCollection.getPlayer(id).playerType = PlayerType.HUMAN
            }
        }

        val noneTypePlayerIdList: List<Int> = playerCollection.getAllNoneId()

        // Filter out none type player, Check whether the command is valid
        val validCommand: Map<Int, List<Command>> = inputCommands.filter { (id, _) ->
            !noneTypePlayerIdList.contains(id)
        }.mapValues { (id, commandList) ->
            commandList.filter { command ->
                command.canSend(playerCollection.getPlayer(id), universeData.universeSettings)
            }
        }

        val playerId3D: List<List<List<List<Int>>>> = playerCollection.getPlayerId3D()

        val commandList: List<Command> = int3DList.pmap { int3D ->
            val playerIdAtGrid: List<Int> = playerId3D[int3D.x][int3D.y][int3D.z]
            val commandPairList: List<Pair<List<Command>,List<Command>>> = playerIdAtGrid.map { fromId ->
                val commandFromPlayer: List<Command> = validCommand.getValue(fromId)
                val (selfCommandList, otherCommandList) = commandFromPlayer.partition { it.toId == fromId }

                val (sameAttachCommandList, commandStoreList) = otherCommandList.partition { command ->
                    val inGrid: Boolean = playerIdAtGrid.contains(command.toId)
                    val sameAttach: Boolean = (playerCollection.getPlayer(fromId).attachedPlayerId ==
                            playerCollection.getPlayer(command.toId).attachedPlayerId)
                    inGrid && sameAttach
                }

                // Execute self command
                for (command in selfCommandList) {
                    command.checkAndExecute(playerCollection.getPlayer(command.toId), universeData.universeSettings)
                }

                Pair(sameAttachCommandList, commandStoreList)
            }

            // Execute command on attached neighbour and return remaining commands
            commandPairList.map { pair ->
                pair.first.forEach { command ->
                    command.checkAndExecute(playerCollection.getPlayer(command.toId), universeData.universeSettings)
                }
                pair.second
            }.flatten()
        }.flatten()

        addToCommandMap(universeData.commandMap, commandList)
    }

    /**
     * First part of the main step
     * Preprocess after the beginning of the turn
     * Save the latest slice and other information of the universe after that
     */
    suspend fun preprocessUniverse() {
        // beginning of the turn
        processMechanism()
        processCommandMap()
        processDeadAndNewPlayer()

        val universeSlice = playerCollection.getUniverseSlice(universeData)
        universeData.updateUniverseReplaceLatest(universeSlice)
        saveLatest()
    }

    /**
     * Post process universe
     * Happens before ai and human input command list
     */
    suspend fun postProcessUniverse(
        humanInputCommands: Map<Int, List<Command>>,
        aiInputCommands: Map<Int, List<Command>>
    ) {
        processCommandInput(humanInputCommands, aiInputCommands)

        // Now the end of the turn
        playerCollection.movePlayer(universeData.universeState, universeData.universeSettings)
        val universeSlice = playerCollection.getUniverseSlice(universeData)
        universeData.updateUniverseDropOldest(universeSlice)
    }

    companion object {
        private val logger = LogManager.getLogger()

        /**
         * Load saved universe by name
         */
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

        /**
         * Transform and add command list to commandMap
         */
        fun addToCommandMap(commandMap: MutableMap<Int, MutableList<Command>>, commandList: List<Command>) {
            val listGroup: Map<Int, List<Command>> = commandList.groupBy { it.toId }
            listGroup.map { (id, commands) -> commandMap.getOrDefault(id, mutableListOf()).addAll(commands)}
        }
    }

}