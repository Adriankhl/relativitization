package relativitization.universe.core

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import relativitization.universe.core.ai.AICollection
import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.PlayerData
import relativitization.universe.core.data.PlayerType
import relativitization.universe.core.data.UniverseData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseData4D
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.UniverseState
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.commands.CommandData
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.data.serializer.DataSerializer.decode
import relativitization.universe.core.data.serializer.DataSerializer.encode
import relativitization.universe.core.global.GlobalMechanismCollection
import relativitization.universe.core.maths.grid.Grids.create3DGrid
import relativitization.universe.core.maths.physics.Int3D
import relativitization.universe.core.maths.physics.Int4D
import relativitization.universe.core.mechanisms.MechanismCollection.processMechanismCollection
import relativitization.universe.core.spacetime.SpacetimeCollection
import relativitization.universe.core.utils.FileUtils
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.core.utils.pmap
import kotlin.random.Random

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
    private val alwaysSaveLatest: Boolean = false,
) {
    // For generating other Random object
    private val masterRandom = Random(universeData.universeSettings.randomSeed)

    private val xDim = universeData.universeSettings.xDim
    private val yDim = universeData.universeSettings.yDim
    private val zDim = universeData.universeSettings.zDim

    // For iteration
    private val int3DList: List<Int3D> = create3DGrid(xDim, yDim, zDim) { x, y, z ->
        Int3D(x, y, z)
    }.flatten().flatten()

    private val universeRandom = Random(masterRandom.nextLong())
    private val int3DRandomMap: Map<Int3D, Random> = int3DList.associateWith {
        Random(masterRandom.nextLong())
    }

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
            val random: Random = int3DRandomMap.getValue(int3D)

            val viewMap: Map<Int, UniverseData3DAtPlayer> = universeData.toUniverseData3DAtGrid(
                Int4D(time, int3D)
            ).idToUniverseData3DAtPlayer()

            playerId3D[int3D.x][int3D.y][int3D.z].map { id ->
                id to AICollection.compute(
                    viewMap.getValue(id),
                    random,
                )
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
     * Update command map of universe data
     *
     * @param newCommandMap command map from
     */
    private fun updateCommandMap(newCommandMap: Map<Int, List<CommandData>>) {
        newCommandMap.keys.sorted().shuffled(universeRandom).forEach { fromId ->
            newCommandMap.getValue(fromId).forEach { commandData ->
                universeData.commandMap.getOrPut(commandData.command.toId) {
                    mutableListOf()
                }.add(commandData)
            }
        }
    }

    /**
     * Compute the time dilation residue and isDilationTurn
     */
    private suspend fun processTimeDilation() {
        playerCollection.getIdSet().pmap {
            val mutablePlayerData: MutablePlayerData = playerCollection.getPlayer(it)

            val dilatedTime: Double = SpacetimeCollection.computeDilatedTime(
                mutablePlayerData.int4D.toInt3D(),
                mutablePlayerData.velocity.toVelocity(),
                universeData.universeSettings,
            )

            // Update time dilation counter
            mutablePlayerData.timeDilationCounter += dilatedTime
            if (mutablePlayerData.timeDilationCounter >= 1.0) {
                mutablePlayerData.timeDilationCounter -= 1.0
                mutablePlayerData.isTimeDilationActionTurn = true
            } else {
                mutablePlayerData.isTimeDilationActionTurn = false
            }
        }
    }

    /**
     * Run all mechanisms on each player, for each player, a list of commands is generated,
     * execute the commands if they are sending to neighbors within the same group of the sender
     * add the rest of generated commands to commandMap
     */
    private suspend fun processMechanism() {
        val time: Int = universeData.universeState.getCurrentTime()
        val playerId3D: List<List<List<List<Int>>>> = playerCollection.getPlayerId3D()

        // Mechanism process, execute produced commands on same group, and return remaining command
        val newCommandMap: Map<Int, List<CommandData>> = int3DList.filter {
            playerId3D[it.x][it.y][it.z].isNotEmpty()
        }.pmap { int3D ->
            val random: Random = int3DRandomMap.getValue(int3D)

            val viewMap: Map<Int, UniverseData3DAtPlayer> = universeData.toUniverseData3DAtGrid(
                Int4D(time, int3D)
            ).idToUniverseData3DAtPlayer()

            val playerIdAtGrid: List<Int> = playerId3D[int3D.x][int3D.y][int3D.z].sorted()

            // Process the mechanisms and compute a map from player id to the generated commands
            val commandMap: Map<Int, List<CommandData>> = playerIdAtGrid.associateWith { id ->
                val universeData3DAtPlayer = viewMap.getValue(id)

                val commandListFromPlayer: List<Command> = processMechanismCollection(
                    playerCollection.getPlayer(id),
                    universeData3DAtPlayer,
                    universeData,
                    random,
                )

                commandListFromPlayer.map { command ->
                    CommandData(
                        command = command,
                        fromId = universeData3DAtPlayer.id,
                        fromInt4D = universeData3DAtPlayer.center,
                    )
                }
            }

            // Differentiate the commands into two groups
            // (1) immediate execution, i.e., sending to neighbors within the same group
            // (2) commands to be saved to command Map
            // In principle, (1) also works for commands sending to self, but it is recommended that
            // the execution should be integrated into the mechanism process
            val commandPairMap: Map<Int, Pair<List<CommandData>, List<CommandData>>> =
                playerIdAtGrid.associateWith { fromId ->
                    val otherCommandList: List<CommandData> = commandMap.getValue(fromId)

                    val (sameGroupCommandList, commandStoreList) = otherCommandList
                        .partition { commandData ->
                            val inGrid: Boolean = playerIdAtGrid.contains(commandData.command.toId)
                            // Ignore get non existing player
                            val sameGroup: Boolean = if (inGrid) {
                                playerCollection.getPlayer(
                                    commandData.fromId
                                ).groupId == playerCollection.getPlayer(commandData.command.toId).groupId
                            } else {
                                false
                            }
                            inGrid && sameGroup
                        }

                    Pair(sameGroupCommandList, commandStoreList)
                }

            // Randomize the execution order by fromId (key of commandPairMap)
            // and execute command on neighbours (players in the same group)
            commandPairMap.keys.sorted().shuffled(random).forEach { fromId ->
                commandPairMap.getValue(fromId).first.forEach { commandData ->
                    commandData.command.checkAndExecute(
                        playerCollection.getPlayer(commandData.command.toId),
                        commandData.fromId,
                        commandData.fromInt4D,
                        universeData.universeSettings,
                    )
                }
            }
            commandPairMap.mapValues { it.value.second }
        }.reduce { acc, map ->
            acc + map
        }

        updateCommandMap(newCommandMap)
    }

    /**
     * Execute commands in parallel
     */
    private suspend fun processCommandMap() = coroutineScope {
        // Remove non-existing player from the command map
        universeData.commandMap.keys.removeAll { !playerCollection.hasPlayer(it) }

        universeData.commandMap.pmap { id, commandList ->
            val playerInt4D: Int4D = playerCollection.getPlayerInt4D(id)

            // Determine the command to be executed by spacetime distance
            val commandExecuteList: List<CommandData> = commandList.filter {
                val timeDelay: Int = SpacetimeCollection.computeTimeDelay(
                    it.fromInt4D.toInt3D(),
                    playerInt4D.toInt3D(),
                    universeData.universeSettings
                )
                val timeDiff: Int = playerInt4D.t - it.fromInt4D.t
                timeDiff >= timeDelay
            }

            // Remove the command to be executed
            commandList.removeAll(commandExecuteList.toSet())

            // Check and execute command
            commandExecuteList.forEach { commandData ->
                commandData.command.checkAndExecute(
                    playerCollection.getPlayer(id),
                    commandData.fromId,
                    commandData.fromInt4D,
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
     * @param originalHumanInputCommandMap map from player id to the command list from this player
     * @param originalAiInputCommandMap map from player id to the command list computed by AI
     */
    private suspend fun processCommandInput(
        originalHumanInputCommandMap: Map<Int, List<Command>>,
        originalAiInputCommandMap: Map<Int, List<Command>>
    ) {
        // Filter out non-existing player
        val humanInputCommandMap: Map<Int, List<Command>> = originalHumanInputCommandMap.filter {
            playerCollection.hasPlayer(it.key)
        }.mapValues { (_, commandList) ->
            // Filter out commands to dead player
            commandList.filter {
                playerCollection.hasPlayer(it.toId)
            }
        }
        val aiInputCommandMap: Map<Int, List<Command>> = originalAiInputCommandMap.filter {
            playerCollection.hasPlayer(it.key)
        }.mapValues { (_, commandList) ->
            // Filter out commands to dead player
            commandList.filter {
                playerCollection.hasPlayer(it.toId)
            }
        }

        // Default all player type to Ai
        for ((id, _) in aiInputCommandMap) {
            if (playerCollection.getPlayer(id).playerType != PlayerType.NONE) {
                playerCollection.getPlayer(id).playerType = PlayerType.AI
            }
        }

        // Then change the player type to human if there is human input
        for ((id, _) in humanInputCommandMap) {
            if (playerCollection.getPlayer(id).playerType != PlayerType.NONE) {
                playerCollection.getPlayer(id).playerType = PlayerType.HUMAN
            }
        }

        val noneTypePlayerIdList: List<Int> = playerCollection.getNoneIdList()

        // Add two input command map, prefer human input commands
        // Add empty command list to player in playerCollection when there is no input
        val inputCommandMap: Map<Int, List<Command>> = aiInputCommandMap.filter { (id, _) ->
            !humanInputCommandMap.containsKey(id)
        } + humanInputCommandMap + playerCollection.getIdSet().filter {
            !humanInputCommandMap.containsKey(it) && !aiInputCommandMap.containsKey(it)
        }.map {
            logger.debug("Player $it has no input command")
            it to listOf()
        }

        // Check whether the command is valid, self execute the command
        val validOtherCommandMap: Map<Int, List<CommandData>> = inputCommandMap.filter { (id, _) ->
            !noneTypePlayerIdList.contains(id)
        }.mapValues { (id, commandList) ->
            val playerData: MutablePlayerData = playerCollection.getPlayer(id)

            val commandSuccessList: List<Pair<CommandData, Boolean>> = commandList.map { command ->
                val success: Boolean = command.checkAndSelfExecuteBeforeSend(
                    playerData,
                    universeData.universeSettings
                ).success

                val commandData = CommandData(
                    command,
                    playerData.playerId,
                    playerData.int4D.toInt4D()
                )

                // self execute Command
                if (success && (command.toId == playerData.playerId)) {
                    command.checkAndExecute(
                        playerCollection.getPlayer(command.toId),
                        playerData.playerId,
                        playerData.int4D.toInt4D(),
                        universeData.universeSettings
                    )

                    Pair(commandData, false)
                } else {
                    Pair(commandData, true)
                }
            }

            commandSuccessList.filter { it.second }.map { it.first }
        }

        val playerId3D: List<List<List<List<Int>>>> = playerCollection.getPlayerId3D()

        val newCommandMap: Map<Int, List<CommandData>> = int3DList.filter {
            playerId3D[it.x][it.y][it.z].isNotEmpty()
        }.pmap { int3D ->
            val random: Random = int3DRandomMap.getValue(int3D)

            val playerIdAtGrid: List<Int> = playerId3D[int3D.x][int3D.y][int3D.z].sorted()
            val commandPairMap: Map<Int, Pair<List<CommandData>, List<CommandData>>> =
                playerIdAtGrid.associateWith { fromId ->
                    val otherCommandList: List<CommandData> = validOtherCommandMap.getValue(fromId)

                    val (sameGroupCommandList, commandStoreList) = otherCommandList
                        .partition { commandData ->
                            val inGrid: Boolean = playerIdAtGrid.contains(commandData.command.toId)
                            // prevent get non existing player
                            val sameGroup: Boolean = if (inGrid) {
                                playerCollection.getPlayer(
                                    commandData.fromId
                                ).groupId == playerCollection.getPlayer(commandData.command.toId).groupId
                            } else {
                                false
                            }
                            inGrid && sameGroup
                        }

                    Pair(sameGroupCommandList, commandStoreList)
                }

            // Shuffled by fromId and execute command on neighbour (same group)
            commandPairMap.keys.sorted().shuffled(random).forEach { fromId ->
                commandPairMap.getValue(fromId).first.forEach { commandData ->
                    commandData.command.checkAndExecute(
                        playerData = playerCollection.getPlayer(commandData.command.toId),
                        fromId = commandData.fromId,
                        fromInt4D = commandData.fromInt4D,
                        universeSettings = universeData.universeSettings,
                    )
                }
            }

            commandPairMap.mapValues { it.value.second }
        }.reduce { acc, map ->
            acc + map
        }

        updateCommandMap(newCommandMap)
    }

    /**
     * First half of a simulation/game step
     * Pre-process the universe before accepting commands from human/AI
     */
    suspend fun preProcessUniverse() {
        // beginning of the turn
        logger.debug("Start preProcessUniverse()")

        logger.debug("Run global mechanisms")
        GlobalMechanismCollection.globalProcess(universeData, universeRandom)

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

        // Save the latest slice and other information of the universe if the option is on
        if (alwaysSaveLatest) {
            logger.debug("Save latest universe data")
            saveLatest()
        }

        logger.debug("Done preProcessUniverse()")
    }

    /**
     * Second half of a simulation/game step
     * Post-process universe based on the received commands from human/AI
     *
     * @param humanInputCommands a map from player id to a list of input commands from human
     * @param aiInputCommands a map from player id to a list of input commands from AI
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
        fun loadUniverseLatest(
            universeName: String,
            programDir: String,
            shouldRandomizeSeed: Boolean
        ): UniverseData {
            val saveDir = "$programDir/saves/$universeName"

            // save settings, setting should be immutable, so only one save is enough
            val originalUniverseSettings: UniverseSettings = decode(
                FileUtils.fileToText(
                    "${saveDir}/universeSetting.json"
                )
            )

            val universeSettings: UniverseSettings = if (shouldRandomizeSeed) {
                originalUniverseSettings.copy(randomSeed = Clock.System.now().epochSeconds)
            } else {
                originalUniverseSettings
            }

            // load latest universe state
            val universeState: UniverseState = decode(
                FileUtils.fileToText(
                    "${saveDir}/latestState.json"
                )
            )

            val latestTime: Int = universeState.getCurrentTime()
            val oldestTime: Int = latestTime - universeSettings.tDim + 1

            val commandMap: MutableMap<Int, MutableList<CommandData>> = decode(
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
            val playerData4D: MutableList<List<List<List<Map<Int, List<PlayerData>>>>>> =
                mutableListOf()

            for (time in oldestTime..latestTime) {
                val playerData3D: List<List<List<MutableMap<Int, MutableList<PlayerData>>>>> =
                    decode(
                        FileUtils.fileToText(
                            "${saveDir}/universeData4DSlice-${time}.json"
                        )
                    )

                playerData3D.flatten().flatten().forEach { playerDataMap ->
                    playerDataMap.values.forEach { playerDataList ->
                        // data needed to be replaced by pointer to older playerData3D to reduce memory usage
                        val toBeReplaced: List<PlayerData> = playerDataList.filter { playerData ->
                            if (timePlayerDataMap.containsKey(playerData.int4D.t)) {
                                timePlayerDataMap.getValue(playerData.int4D.t)
                                    .containsKey(playerData.playerId)
                            } else {
                                false
                            }
                        }

                        // replace the data with
                        val replaceWith: List<PlayerData> = toBeReplaced.map { playerData ->
                            timePlayerDataMap.getValue(playerData.int4D.t)
                                .getValue(playerData.playerId)
                        }

                        // Replace data
                        playerDataList.removeAll(toBeReplaced.toSet())
                        playerDataList.addAll(replaceWith)

                        // Store data in int4DPlayerDataMap
                        playerDataList.forEach { playerData ->
                            timePlayerDataMap.getOrPut(playerData.int4D.t) {
                                mutableMapOf()
                            }[playerData.playerId] = playerData
                        }
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
    }
}