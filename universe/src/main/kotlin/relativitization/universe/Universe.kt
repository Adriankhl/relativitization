package relativitization.universe

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import relativitization.universe.ai.AICollection
import relativitization.universe.data.*
import relativitization.universe.data.commands.Command
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer.decode
import relativitization.universe.data.serializer.DataSerializer.encode
import relativitization.universe.global.GlobalMechanismCollection
import relativitization.universe.maths.grid.Grids.create3DGrid
import relativitization.universe.maths.physics.Int3D
import relativitization.universe.maths.physics.Int4D
import relativitization.universe.maths.physics.Intervals.intDelay
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.maths.random.Rand
import relativitization.universe.mechanisms.MechanismCollection.processMechanismCollection
import relativitization.universe.utils.FileUtils
import relativitization.universe.utils.RelativitizationLogManager
import relativitization.universe.utils.pmap

/**
 * Main class representing the 4D universe
 * Main stepping function: preProcessUniverse and postProcessUniverse
 *
 * @param universeData the core data of the universe
 * @param programDir the location of the program directories, "." for desktop and context.filesDir for android
 * @param saveWhenInit save all when initializing the universe
 * @param alwaysSaveLatest always save the latest slice for loading
 */
class Universe(
    private val universeData: UniverseData,
    private val programDir: String = ".",
    private val saveWhenInit: Boolean = false,
    private val alwaysSaveLatest: Boolean = false
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
        universeData.getCurrentPlayerDataList().forEach { playerCollection.addPlayer(it) }


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
     * Get all current player data as a list
     */
    fun getCurrentPlayerDataList(): List<PlayerData> = universeData.getCurrentPlayerDataList()

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
     * Compute commands by AI for all player in a coroutine
     */
    suspend fun computeAICommands(): Map<Int, List<Command>> = coroutineScope {
        val time: Int = universeData.universeState.getCurrentTime()
        val playerId3D: List<List<List<List<Int>>>> = playerCollection.getPlayerId3D()
        int3DList.pmap { int3D ->
            val viewMap = universeData.toUniverseData3DAtGrid(
                Int4D(time, int3D)
            ).idToUniverseData3DAtPlayer()

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
        FileUtils.mkdirs(saveDir)

        // save universe 4D slice
        FileUtils.textToFile(
            text = encode(universeData.universeData4D.getLatest()),
            path = "${saveDir}/universeData4DSlice-${latestTime}.json"
        )

        // save state
        FileUtils.textToFile(
            text = encode(universeData.universeState),
            path = "${saveDir}/universeState-${latestTime}.json"
        )

        // save settings, setting should be immutable, so only one save is enough
        FileUtils.textToFile(
            text = encode(universeData.universeSettings),
            path = "${saveDir}/universeSetting.json",
        )

        // save commands
        FileUtils.textToFile(
            text = encode(universeData.commandMap),
            path = "${saveDir}/commandMap-${latestTime}.json",
        )

        // save science data
        FileUtils.textToFile(
            text = encode(universeData.universeGlobalData),
            path = "${saveDir}/universeGlobalData-${latestTime}.json",
        )

        // Additionally, save state to latestState.json for loading
        FileUtils.textToFile(
            text = encode(universeData.universeState),
            path = "${saveDir}/latestState.json",
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

        FileUtils.mkdirs(saveDir)

        for (i in oldUniverseData4D.indices) {
            FileUtils.textToFile(
                text = encode(oldUniverseData4D[i]),
                path = "${saveDir}/universeData4DSlice-${oldestTime + i}.json",
            )
        }

        // save settings, setting should be immutable, so only one save is enough
        FileUtils.textToFile(
            text = encode(universeData.universeSettings),
            path = "${saveDir}/universeSetting.json",
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
            val viewMap = universeData.toUniverseData3DAtGrid(
                Int4D(time, int3D)
            ).idToUniverseData3DAtPlayer()

            val playerIdAtGrid: List<Int> = playerId3D[int3D.x][int3D.y][int3D.z]

            // Process the mechanisms and compute a map from player id and the command it produces
            val commandMap: Map<Int, List<Command>> = playerIdAtGrid.associateWith { id ->
                val universeData3DAtPlayer = viewMap.getValue(id)

                val commandListFromPlayer: List<Command> = processMechanismCollection(
                    playerCollection.getPlayer(id),
                    universeData3DAtPlayer,
                    universeData
                )

                commandListFromPlayer
            }

            // Differentiate the commands should be executed immediately, e.g., self and same group player
            // or commands to be saved to command Map
            // In principle, this shouldn't contain self commands since they should be integrated in the mechanism process
            val commandPairList: List<Pair<List<Command>, List<Command>>> =
                playerIdAtGrid.map { fromId ->
                    val otherCommandList: List<Command> = commandMap.getValue(fromId)

                    val (sameGroupCommandList, commandStoreList) = otherCommandList.partition { command ->
                        val inGrid: Boolean = playerIdAtGrid.contains(command.toId)
                        // prevent get non existing player
                        val sameGroup: Boolean = if (inGrid) {
                            playerCollection.getPlayer(
                                command.fromId
                            ).groupId == playerCollection.getPlayer(command.toId).groupId
                        } else {
                            false
                        }
                        inGrid && sameGroup
                    }

                    Pair(sameGroupCommandList, commandStoreList)
                }

            // Shuffled by fromId and execute command on neighbour (same group)
            val sameGroupCommandList: List<Command> = commandPairList.map {
                it.first
            }.shuffled(Rand.rand()).flatten()
            sameGroupCommandList.forEach { command ->
                command.checkAndExecute(
                    playerCollection.getPlayer(command.toId),
                    universeData.universeSettings
                )
            }

            // filter out dead player
            commandPairList.map { it.second }.flatten().filter {
                playerCollection.hasPlayer(it.toId)
            }
        }.flatten()

        addToCommandMap(universeData.commandMap, commandList)
    }

    /**
     * Execute commands in parallel
     */
    private suspend fun processCommandMap() = coroutineScope {
        // Remove non-existing player from the command map
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
            commandExecuteList.forEach { command ->
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
     * Process human and AI command input
     *
     * @param originalHumanInputCommands map from player id to the command list from this player
     * @param originalAiInputCommands map from player id to the command list computed by AI
     */
    private suspend fun processCommandInput(
        originalHumanInputCommands: Map<Int, List<Command>>,
        originalAiInputCommands: Map<Int, List<Command>>
    ) {
        // Filter out non-existing player
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
        val validOtherCommandMap: Map<Int, List<Command>> = inputCommands.filter { (id, _) ->
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
                    val otherCommandList: List<Command> = validOtherCommandMap.getValue(fromId)

                    val (sameGroupCommandList, commandStoreList) = otherCommandList.partition { command ->
                        val inGrid: Boolean = playerIdAtGrid.contains(command.toId)
                        // prevent get non existing player
                        val sameGroup: Boolean = if (inGrid) {
                            playerCollection.getPlayer(
                                command.fromId
                            ).groupId == playerCollection.getPlayer(command.toId).groupId
                        } else {
                            false
                        }
                        inGrid && sameGroup
                    }

                    Pair(sameGroupCommandList, commandStoreList)
                }

            // Shuffled by fromId and execute command on neighbour (same group)
            val sameGroupCommandList: List<Command> = commandPairList.map {
                it.first
            }.shuffled(Rand.rand()).flatten()
            sameGroupCommandList.forEach { command ->
                command.checkAndExecute(
                    playerCollection.getPlayer(command.toId),
                    universeData.universeSettings
                )
            }

            // return remaining commands
            commandPairList.map { it.second }.flatten().filter {
                playerCollection.hasPlayer(it.toId)
            }
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
     * Happens before AI and human input command list
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

        logger.debug("Process dead players and new players")
        processDeadAndNewPlayer()

        logger.debug("Get new universe slice in postProcessUniverse()")
        val universeSlice = playerCollection.getUniverseSlice(universeData)

        logger.debug("Add the latest slice of the universe and drop oldest slice")
        universeData.updateUniverseDropOldest(universeSlice)

        logger.debug("Done postProcessUniverse()")
    }

    /**
     * A step of the simulation with only AI
     */
    fun pureAIStep() {
        runBlocking {
            val aiCommandMap: Map<Int, List<Command>> = computeAICommands()

            postProcessUniverse(
                mapOf(),
                aiCommandMap
            )
            preProcessUniverse()
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()

        /**
         * Load saved universe by name
         */
        fun loadUniverseLatest(universeName: String, programDir: String): UniverseData {
            val saveDir = "$programDir/saves/$universeName"

            // save settings, setting should be immutable, so only one save is enough
            val universeSettings: UniverseSettings = decode(
                FileUtils.fileToText(
                    "${saveDir}/universeSetting.json"
                )
            )

            // load latest universe state
            val universeState: UniverseState = decode(
                FileUtils.fileToText(
                    "${saveDir}/latestState.json"
                )
            )

            val latestTime: Int = universeState.getCurrentTime()
            val oldestTime: Int = latestTime - universeSettings.tDim + 1

            val commandMap: MutableMap<Int, MutableList<Command>> = decode(
                FileUtils.fileToText(
                    "${saveDir}/commandMap-${latestTime}.json"
                )
            )

            val universeGlobalData: UniverseGlobalData = decode(
                FileUtils.fileToText(
                    "${saveDir}/universeGlobalData-${latestTime}.json"
                )
            )

            // For reusing player data in the history, e.g. when dealing with after Image
            val timePlayerDataMap: MutableMap<Int, MutableMap<Int, PlayerData>> = mutableMapOf()
            // Store the player 4D data
            val playerData4D: MutableList<List<List<List<List<PlayerData>>>>> = mutableListOf()
            for (time in oldestTime..latestTime) {
                val playerData3D: List<List<List<MutableList<PlayerData>>>> = decode(
                    FileUtils.fileToText(
                        "${saveDir}/universeData4DSlice-${time}.json"
                    )
                )

                playerData3D.flatten().flatten().forEach { playerDataList ->
                    // data needed to be replaced by pointer to older playerData3D to reduce memory usage
                    val toBeReplaced: List<PlayerData> = playerDataList.filter { playerData ->
                        if (timePlayerDataMap.containsKey(playerData.int4D.t)) {
                            timePlayerDataMap.getValue(playerData.int4D.t).containsKey(playerData.playerId)
                        } else {
                            false
                        }
                    }

                    // replace the data with
                    val replaceWith: List<PlayerData> = toBeReplaced.map { playerData ->
                        timePlayerDataMap.getValue(playerData.int4D.t).getValue(playerData.playerId)
                    }

                    // Replace data
                    playerDataList.removeAll(toBeReplaced)
                    playerDataList.addAll(replaceWith)

                    // Store data in int4DPlayerDataMap
                    playerDataList.forEach { playerData->
                        timePlayerDataMap.getOrDefault(playerData.int4D.t, mutableMapOf())[playerData.playerId] =
                            playerData
                    }
                }

                // Add the replaced 3d data to 4d data
                playerData4D.add(playerData3D)
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
            listGroup.forEach { (id, commandList) ->
                val shuffledCommandList: List<Command> = Rand.groupByAndShuffle(
                    commandList
                ) {
                    it.fromId
                }
                commandMap.getOrPut(id) { mutableListOf() }.addAll(shuffledCommandList)
            }
        }
    }
}