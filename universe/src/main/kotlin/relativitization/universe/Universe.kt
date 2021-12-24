package relativitization.universe

import kotlinx.coroutines.coroutineScope
import relativitization.universe.ai.AICollection
import relativitization.universe.data.*
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.physics.Int3D
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer.decode
import relativitization.universe.data.serializer.DataSerializer.encode
import relativitization.universe.global.GlobalMechanismCollection
import relativitization.universe.maths.grid.Grids.create3DGrid
import relativitization.universe.maths.physics.Intervals.intDelay
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.mechanisms.MechanismCollection.processMechanismCollection
import relativitization.universe.utils.RelativitizationLogManager
import relativitization.universe.utils.pmap
import java.io.File
import kotlin.math.log

/**
 * Main class representing the 4D universe
 * Main stepping function: preProcessUniverse and postProcessUniverse
 *
 * @param universeData the core data of the universe
 * @param programDir the location of the program directories, "." for desktop and context.filesDir for android
 * @param saveWhenInit save all when initializing the universe
 * @param alwaysSaveLatest always save latest slice for loading
 */
class Universe(
    private val universeData: UniverseData,
    private val programDir: String,
    private val saveWhenInit: Boolean = true,
    private val alwaysSaveLatest: Boolean = true
) {

    private val xDim = universeData.universeSettings.xDim
    private val yDim = universeData.universeSettings.yDim
    private val zDim = universeData.universeSettings.zDim

    // For iteration
    private val int3DList: List<Int3D> = create3DGrid(xDim, yDim, zDim) { x, y, z ->
        Int3D(x, y, z)
    }.flatten().flatten()

    private val playerCollection: PlayerCollection = PlayerCollection(
        xDim,
        yDim,
        zDim,
        universeData.universeSettings.groupEdgeLength
    )

    init {
        // for each player data at the latest time slice, create player object and add to universe3D
        universeData.getLatestPlayerDataList().forEach { playerCollection.addPlayer(it) }


        if (saveWhenInit) {
            saveAll()
        }
    }

    /**
     * Get universe name
     */
    fun getUniverseName(): String {
        return universeData.universeSettings.universeName
    }

    /**
     * Get current universe time
     */
    fun getCurrentUniverseTime(): Int {
        return universeData.universeState.getCurrentTime()
    }

    /**
     * Get dead id list
     */
    fun getDeadIdList(): List<Int> {
        return playerCollection.getDeadIdList()
    }

    /**
     * True if player is alive
     * Should only return if canAccess is true
     */
    fun isAlive(id: Int): Boolean {
        return playerCollection.hasPlayer(id)
    }

    /**
     * Get all available player
     * Should only return if canAccess is true
     */
    fun availablePlayers(): List<Int> {
        return playerCollection.getHumanOrAiIdList()
    }

    /**
     * Get all suggested available player
     */
    fun availableHumanPLayers(): List<Int> {
        return playerCollection.getHumanIdList()
    }

    /**
     * Get universe 3d view for (human) player
     * Should only return if canAccess is true
     */
    fun getUniverse3DViewAtPlayer(id: Int): UniverseData3DAtPlayer {
        val int4D: Int4D = playerCollection.getPlayerInt4D(id)
        logger.debug("getUniverse3DViewAtPlayer - int4D: $int4D, player id: $id")
        return universeData.toUniverseData3DAtGrid(int4D).idToUniverseData3DAtPlayer().getValue(id)
    }

    /**
     * Compute commands by ai for all player in a coroutine
     */
    suspend fun computeAICommands(): Map<Int, List<Command>> = coroutineScope {
        val time: Int = universeData.universeState.getCurrentTime()
        val playerId3D: List<List<List<List<Int>>>> = playerCollection.getPlayerId3D()
        int3DList.pmap { int3D ->
            val viewMap =
                universeData.toUniverseData3DAtGrid(Int4D(time, int3D)).idToUniverseData3DAtPlayer()
            playerId3D[int3D.x][int3D.y][int3D.z].map { id ->
                id to AICollection.compute(viewMap.getValue(id))
            }
        }.flatten().toMap()
    }

    /**
     * Save Latest universe data
     */
    private fun saveLatest() {
        val saveDir = "$programDir/saves/${universeData.universeSettings.universeName}"
        val latestTime: Int = universeData.universeState.getCurrentTime()

        // Make Directory
        File(saveDir).mkdirs()

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

        // save science data
        File("${saveDir}/universeGlobalData-${latestTime}.json").writeText(
            encode(universeData.universeGlobalData)
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
        val saveDir = "$programDir/saves/${universeData.universeSettings.universeName}"
        val latestTime: Int = universeData.universeState.getCurrentTime()
        val oldestTime: Int = latestTime - universeData.universeSettings.tDim + 1
        val oldUniverseData4D = universeData.universeData4D.getAllExcludeLatest()

        File(saveDir).mkdirs()

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
     * Compute the time dilation residue and isDilationTurn
     */
    private suspend fun processTimeDilation() {
        playerCollection.getIdList().pmap {
            val mutablePlayerData: MutablePlayerData = playerCollection.getPlayer(it)

            val gamma: Double = Relativistic.gamma(
                mutablePlayerData.velocity.toVelocity(),
                universeData.universeSettings.speedOfLight
            )

            // Update dilated time residue
            mutablePlayerData.dilatedTimeResidue += 1.0 / gamma

            // Check whether this should be a time dilation action turn
            // the residue should always be smaller than 1
            if (mutablePlayerData.dilatedTimeResidue >= 1.0) {
                mutablePlayerData.dilatedTimeResidue -= 1.0
                mutablePlayerData.isDilationActionTurn = true
            } else {
                mutablePlayerData.isDilationActionTurn = false
            }
        }
    }

    /**
     * Run all the mechanism, add generated commands to commandMap
     */
    private suspend fun processMechanism() {
        val time: Int = universeData.universeState.getCurrentTime()
        val playerId3D: List<List<List<List<Int>>>> = playerCollection.getPlayerId3D()

        // Mechanism process, execute produced commands on same group, and return remaining command
        val commandList: List<Command> = int3DList.pmap { int3D ->
            val viewMap =
                universeData.toUniverseData3DAtGrid(Int4D(time, int3D)).idToUniverseData3DAtPlayer()

            val playerIdAtGrid: List<Int> = playerId3D[int3D.x][int3D.y][int3D.z]

            val commandListAtGrid: List<Command> = playerIdAtGrid.map { id ->
                val universeData3DAtPlayer = viewMap.getValue(id)

                val commandListFromPlayer: List<Command> = processMechanismCollection(
                    playerCollection.getPlayer(id),
                    universeData3DAtPlayer,
                    universeData
                )


                commandListFromPlayer
            }.flatten()

            // Differentiate the commands the should be executed immediately, e.g., self and same group player
            // or commands to be saved to command Map
            // In principle, this shouldn't contain self commands since they should be integrated in the mechanism process
            val (commandExecuteList, commandStoreList) = commandListAtGrid.partition {
                val inGrid: Boolean = playerIdAtGrid.contains(it.toId)
                // prevent get non existing player
                val sameGroup: Boolean = if (inGrid) {
                    playerCollection.getPlayer(
                        it.fromId
                    ).groupId == playerCollection.getPlayer(it.toId).groupId
                } else {
                    false
                }
                inGrid && sameGroup
            }

            // Check and execute immediate command
            for (command in commandExecuteList) {
                command.checkAndExecute(
                    playerCollection.getPlayer(command.toId),
                    universeData.universeSettings
                )
            }

            // filter out dead player
            commandStoreList.filter { playerCollection.hasPlayer(it.toId) }
        }.flatten()

        addToCommandMap(universeData.commandMap, commandList)
    }

    /**
     * Execute commands in parallel
     */
    private suspend fun processCommandMap() = coroutineScope {
        // Remove non existing player from the command map
        val noIdList: List<Int> =
            universeData.commandMap.keys.filter { !playerCollection.hasPlayer(it) }

        for (id in noIdList) {
            universeData.commandMap.remove(id)
        }

        universeData.commandMap.pmap { id, commandList ->
            val playerInt4D: Int4D = playerCollection.getPlayerInt4D(id)

            // Determine the command to be executed by spacetime distance
            val commandExecuteList: List<Command> = commandList.filter {
                val timeDelay: Int = intDelay(
                    it.fromInt4D.toInt3D(),
                    playerInt4D.toInt3D(),
                    universeData.universeSettings.speedOfLight
                )
                val timeDiff: Int = playerInt4D.t - it.fromInt4D.t
                timeDiff >= timeDelay
            }

            // Remove the command to be executed
            commandList.removeAll(commandExecuteList)

            // Check and execute command
            for (command in commandExecuteList) {
                command.checkAndExecute(
                    playerCollection.getPlayer(id),
                    universeData.universeSettings
                )
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
     * @param originalHumanInputCommands map from player id to the command list from this player
     * @param originalAiInputCommands map from player id to the command list computed by ai
     */
    private suspend fun processCommandInput(
        originalHumanInputCommands: Map<Int, List<Command>>,
        originalAiInputCommands: Map<Int, List<Command>>
    ) {
        // Filter out non existing player
        val humanInputCommands: Map<Int, List<Command>> = originalHumanInputCommands.filter {
            playerCollection.hasPlayer(it.key)
        }.mapValues { (_, commandList) ->
            // Filter out commands to dead player
            commandList.filter {
                playerCollection.hasPlayer(it.toId)
            }
        }
        val aiInputCommands: Map<Int, List<Command>> = originalAiInputCommands.filter {
            playerCollection.hasPlayer(it.key)
        }.mapValues { (_, commandList) ->
            // Filter out commands to dead player
            commandList.filter {
                playerCollection.hasPlayer(it.toId)
            }
        }

        // Add two input command map, prefer human input commands
        // Add empty command list to player in playerCollection when there is no input
        val inputCommands: Map<Int, List<Command>> = aiInputCommands.filter { (id, _) ->
            !humanInputCommands.containsKey(id)
        } + humanInputCommands + playerCollection.getIdList().filter {
            !humanInputCommands.containsKey(it) && !aiInputCommands.containsKey(it)
        }.map {
            logger.debug("Player $it has no input command")
            it to listOf()
        }

        // Default all player type to Ai
        for ((id, _) in aiInputCommands) {
            if (playerCollection.getPlayer(id).playerType != PlayerType.NONE) {
                playerCollection.getPlayer(id).playerType = PlayerType.AI
            }
        }

        // Then change the player type to human if there is human input
        for ((id, _) in humanInputCommands) {
            if (playerCollection.getPlayer(id).playerType != PlayerType.NONE) {
                playerCollection.getPlayer(id).playerType = PlayerType.HUMAN
            }
        }

        val noneTypePlayerIdList: List<Int> = playerCollection.getNoneIdList()

        // Check whether the command is valid, self execute the command
        val validOtherCommands: Map<Int, List<Command>> = inputCommands.filter { (id, _) ->
            !noneTypePlayerIdList.contains(id)
        }.mapValues { (id, commandList) ->
            val playerData: MutablePlayerData = playerCollection.getPlayer(id)
            commandList.filter { command ->
                val success: Boolean = command.checkAndSelfExecuteBeforeSend(
                    playerData,
                    universeData.universeSettings
                ).success

                // self execute Command
                if (success && (command.toId == playerData.playerId)) {
                    command.checkAndExecute(
                        playerCollection.getPlayer(command.toId),
                        universeData.universeSettings
                    )
                    false
                } else {
                    success
                }
            }
        }

        val playerId3D: List<List<List<List<Int>>>> = playerCollection.getPlayerId3D()

        val commandList: List<Command> = int3DList.pmap { int3D ->
            val playerIdAtGrid: List<Int> = playerId3D[int3D.x][int3D.y][int3D.z]
            val commandPairList: List<Pair<List<Command>, List<Command>>> =
                playerIdAtGrid.map { fromId ->
                    val otherCommandList: List<Command> = validOtherCommands.getValue(fromId)

                    val (sameGroupCommandList, commandStoreList) = otherCommandList.partition { command ->
                        val inGrid: Boolean = playerIdAtGrid.contains(command.toId)
                        val sameGroup: Boolean = (playerCollection.getPlayer(fromId).groupId ==
                                playerCollection.getPlayer(command.toId).groupId)
                        inGrid && sameGroup
                    }

                    Pair(sameGroupCommandList, commandStoreList)
                }

            // Execute command on neighbour (same group) and return remaining commands
            commandPairList.map { pair ->
                pair.first.forEach { command ->
                    command.checkAndExecute(
                        playerCollection.getPlayer(command.toId),
                        universeData.universeSettings
                    )
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
    suspend fun preProcessUniverse() {
        // beginning of the turn
        logger.debug("Start preProcessUniverse()")

        logger.debug("Run global mechanisms")
        GlobalMechanismCollection.globalProcess(universeData)

        logger.debug("Process time dilation")
        processTimeDilation()

        logger.debug("Process mechanism")
        processMechanism()

        logger.debug("Process command map")
        processCommandMap()

        logger.debug("Process dead players and new players")
        processDeadAndNewPlayer()

        // Sync all data component to ensure consistency before universe data update
        logger.debug("Sync all player data component in preProcessUniverse()")
        playerCollection.syncAllPlayerDataComponent()

        logger.debug("Get new universe slice in preProcessUniverse()")
        val universeSlice = playerCollection.getUniverseSlice(universeData)

        logger.debug("Replace the latest slice of the universe")
        universeData.updateUniverseReplaceLatest(universeSlice)

        if (alwaysSaveLatest) {
            logger.debug("Save latest universe data")
            saveLatest()
        }

        logger.debug("Done preProcessUniverse()")
    }

    /**
     * Post process universe
     * Happens before ai and human input command list
     */
    suspend fun postProcessUniverse(
        humanInputCommands: Map<Int, List<Command>>,
        aiInputCommands: Map<Int, List<Command>>
    ) {
        logger.debug("Start postProcessUniverse()")
        processCommandInput(humanInputCommands, aiInputCommands)

        // Now the end of the turn
        logger.debug("Move player in player collection")
        playerCollection.movePlayer(universeData.universeState, universeData.universeSettings)

        // Sync all data component to ensure consistency before universe data update
        logger.debug("Sync all player data component in postProcessUniverse()")
        playerCollection.syncAllPlayerDataComponent()

        logger.debug("Get new universe slice in postProcessUniverse()")
        val universeSlice = playerCollection.getUniverseSlice(universeData)

        logger.debug("Add the latest slice of the universe and drop oldest slice")
        universeData.updateUniverseDropOldest(universeSlice)

        logger.debug("Done postProcessUniverse()")
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()

        /**
         * Load saved universe by name
         */
        fun loadUniverseLatest(universeName: String, programDir: String): UniverseData {
            val saveDir = "$programDir/saves/$universeName"

            // save settings, setting should be immutable, so only one save is enough
            val universeSettings: UniverseSettings =
                decode(File("${saveDir}/universeSetting.json").readText())

            // load latest universe state
            val universeState: UniverseState =
                decode(File("${saveDir}/latestState.json").readText())

            val latestTime: Int = universeState.getCurrentTime()
            val oldestTime: Int = latestTime - universeSettings.tDim + 1

            val commandMap: MutableMap<Int, MutableList<Command>> = decode(
                File("${saveDir}/commandMap-${latestTime}.json").readText()
            )

            val universeGlobalData: UniverseGlobalData = decode(
                File("${saveDir}/universeGlobalData-${latestTime}.json").readText()
            )

            val playerData4D: MutableList<List<List<List<List<PlayerData>>>>> = mutableListOf()
            for (time in oldestTime..latestTime) {
                playerData4D.add(decode(File("${saveDir}/universeData4DSlice-${time}.json").readText()))
            }

            val universeData4D = UniverseData4D(playerData4D)

            return UniverseData(
                universeData4D = universeData4D,
                universeSettings = universeSettings,
                universeState = universeState,
                commandMap = commandMap,
                universeGlobalData = universeGlobalData
            )
        }

        /**
         * Transform and add command list to commandMap
         */
        fun addToCommandMap(
            commandMap: MutableMap<Int, MutableList<Command>>,
            commandList: List<Command>
        ) {
            val listGroup: Map<Int, List<Command>> = commandList.groupBy { it.toId }
            listGroup.map { (id, commands) ->
                commandMap.getOrPut(id) { mutableListOf() }.addAll(commands)
            }
        }
    }

}