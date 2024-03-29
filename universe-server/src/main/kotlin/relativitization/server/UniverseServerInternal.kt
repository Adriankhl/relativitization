package relativitization.server

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import relativitization.universe.core.Universe
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethodCollection
import relativitization.universe.core.utils.CoroutineBoolean
import relativitization.universe.core.utils.CoroutineVar
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.game.UniverseServerSettings
import relativitization.universe.game.communication.CheckIsPlayerDeadMessage
import relativitization.universe.game.communication.DeregisterPlayerMessage
import relativitization.universe.game.communication.PlayerInputMessage
import relativitization.universe.game.communication.RegisterPlayerMessage
import relativitization.universe.game.communication.UniverseData3DMessage
import relativitization.universe.game.communication.UniverseServerStatusMessage
import java.io.File

class UniverseServerInternal(
    var universeServerSettings: UniverseServerSettings,
    val serverAddress: String,
    val serverPort: Int,
) {
    private val mutex: Mutex = Mutex()

    // Data of universe
    private var universe: Universe = Universe(
        universeData = GenerateUniverseMethodCollection.generate(
            GenerateSettings()
        ),
        programDir = universeServerSettings.programDir,
        saveWhenInit = false,
        alwaysSaveLatest = true,
    )

    // Current universe time
    private var currentUniverseTime: Int = universe.getCurrentUniverseTime()

    // Whether there is already a universe
    private val hasUniverse: CoroutineBoolean = CoroutineBoolean(false)

    // Whether the universe is running
    private val isUniverseRunning: CoroutineBoolean = CoroutineBoolean(false)

    // is waiting input from human
    // client can only get data and post command list if this is true
    val isServerWaitingInput: CoroutineBoolean = CoroutineBoolean(false)

    // Whether the data process is done
    private val isProcessDone: CoroutineBoolean = CoroutineBoolean(false)

    // wait beginning time in millisecond, used to calculate the time limit to stop waiting
    private val waitBeginTime: CoroutineVar<Long> = CoroutineVar(System.currentTimeMillis())

    // map from registered player id to password
    private val humanIdPasswordMap: MutableMap<Int, String> = mutableMapOf()

    // Available id list
    private val availableIdList: MutableList<Int> = mutableListOf()

    // Available (suggested) human id list
    private val availableHumanIdList: MutableList<Int> = mutableListOf()

    // Dead id list
    private val deadIdList: MutableList<Int> = mutableListOf()

    // command Map for human input
    private val humanCommandMap: MutableMap<Int, List<Command>> = mutableMapOf()

    // ai computed command map
    private val aiCommandMap: MutableMap<Int, List<Command>> = mutableMapOf()

    /**
     * Start the universe
     */
    suspend fun start() = coroutineScope {
        while (isActive) {
            logger.trace("Server internal running")
            delay(1000)

            if (isUniverseRunning.isTrue()) {
                mutex.withLock {
                    if (allHumanInputReady() ||
                        !isServerWaitingInput.isTrue() ||
                        exceedTimeLimit()
                    ) {
                        isServerWaitingInput.set(false)
                        setTimeLeftTo(0L)
                        logger.debug("Not accepting new input")
                    }
                }

                if (!isServerWaitingInput.isTrue() && !isProcessDone.isTrue()) {
                    // Post-process then pre-process since the universe accept input in the middle of game turn
                    universe.postProcessUniverse(humanCommandMap, aiCommandMap)
                    universe.preProcessUniverse()

                    // Clear and update the command maps and player id list
                    updateCommandMapAndIdList()

                    // Update current universe time
                    currentUniverseTime = universe.getCurrentUniverseTime()

                    // Clear inactive (no input received) player or add back inactive to wait for their input
                    if (universeServerSettings.clearInactivePerTurn) {
                        clearInactive()
                    } else {
                        addInactive()
                    }

                    isProcessDone.set(true)
                }


                // Compute AI commands after universe processing is done
                if (isProcessDone.isTrue()) {
                    // Start to accept human input
                    setTimeLeftTo(universeServerSettings.waitTimeLimit)
                    isServerWaitingInput.set(true)

                    logger.debug("Start accepting new input")

                    aiCommandMap.putAll(universe.computeAICommands())

                    // Restart wait timer after ai command has been computed
                    // To avoid heavy ai computation blocking human input from GUI
                    setTimeLeftTo(universeServerSettings.waitTimeLimit)

                    logger.debug("AI done computation")

                    isProcessDone.set(false)
                }
            }
        }
    }

    /**
     * Stop the universe
     *
     * @param job the job running the start() function/
     */
    suspend fun stop(job: Job) {
        isUniverseRunning.set(false)
        job.cancelAndJoin()
    }

    /**
     * Is waiting for input or waiting to start
     */
    private suspend fun isWaiting(): Boolean {
        return ((hasUniverse.isTrue() && !isUniverseRunning.isTrue()) ||
                (isUniverseRunning.isTrue() && isServerWaitingInput.isTrue())
                )
    }

    /**
     * Clear and update all command map and player id list
     */
    private fun updateCommandMapAndIdList() {
        // Clear command map for next turn input
        humanCommandMap.clear()
        aiCommandMap.clear()
        availableIdList.clear()
        availableHumanIdList.clear()

        // Also clear deadIdList
        deadIdList.clear()

        // Change available id
        availableIdList.addAll(universe.availablePlayers())
        availableHumanIdList.addAll(universe.availableHumanPLayers())

        // Get new dead id list
        deadIdList.addAll(universe.getDeadIdList())
    }

    /**
     * Clear inactive player registration: id and password
     */
    private fun clearInactive() {
        humanIdPasswordMap.keys.removeAll { id ->
            // Don't clear dead player
            !availableHumanIdList.contains(id) && !deadIdList.contains(id)
        }
    }

    /**
     * Add back inactive player to available human player if the player is available
     */
    private fun addInactive() {
        availableHumanIdList.addAll(availableIdList.filter { humanIdPasswordMap.keys.contains(it) })
    }

    /**
     * Time left for waiting, can be negative
     */
    private suspend fun timeLeft(): Long = universeServerSettings.waitTimeLimit * 1000L -
            (System.currentTimeMillis() - waitBeginTime.get())


    /**
     * Whether the wait time has exceed the time limit
     */
    private suspend fun exceedTimeLimit(): Boolean =
        timeLeft() < 0

    /**
     * Set time left to
     *
     * @param time time left in seconds
     */
    private suspend fun setTimeLeftTo(time: Long) {
        waitBeginTime.set(
            universeServerSettings.waitTimeLimit * 1000L + System.currentTimeMillis() - time * 1000L
        )
    }

    /**
     * Whether all human input is ready
     * Note that extra human input can override ai computed input, but we won't wait for that
     */
    private fun allHumanInputReady(): Boolean {
        return humanCommandMap.keys.containsAll(availableHumanIdList)
    }

    /**
     * Human input with this function
     *
     * @return successful input or not
     */
    suspend fun humanInput(playerInputMessage: PlayerInputMessage): Boolean {
        mutex.withLock {
            return if (
                (humanIdPasswordMap.keys.contains(playerInputMessage.id)) &&
                (humanIdPasswordMap.getValue(playerInputMessage.id) == playerInputMessage.password) &&
                (isServerWaitingInput.isTrue())
            ) {
                humanCommandMap[playerInputMessage.id] = playerInputMessage.commandList
                true
            } else {
                false
            }
        }
    }

    /**
     * Set new universe
     */
    suspend fun setUniverse(newUniverse: Universe) {
        logger.debug("Set universe")
        mutex.withLock {
            if (!isUniverseRunning.isTrue()) {
                universe = newUniverse
                updateCommandMapAndIdList()
                humanIdPasswordMap.clear()
                currentUniverseTime = universe.getCurrentUniverseTime()
                hasUniverse.set(true)
            }
        }
    }

    /**
     * Set universe setting
     */
    suspend fun setUniverseServerSettings(newUniverseServerSettings: UniverseServerSettings) {
        mutex.withLock {
            universeServerSettings = newUniverseServerSettings
        }
    }

    /**
     * Get universe status
     */
    suspend fun getUniverseStatusMessage(): UniverseServerStatusMessage {
        mutex.withLock {
            return UniverseServerStatusMessage(
                universeName = universe.getUniverseName(),
                success = true,
                hasUniverse = hasUniverse.isTrue(),
                isUniverseRunning = isUniverseRunning.isTrue(),
                isServerWaitingInput = isServerWaitingInput.isTrue(),
                timeLeft = timeLeft(),
                currentUniverseTime = currentUniverseTime,
            )
        }
    }

    /**
     * Get saved universe name
     */
    suspend fun getSavedUniverse(): List<String> {
        mutex.withLock {
            return File("${universeServerSettings.programDir}/saves").list()?.toList() ?: listOf()
        }
    }

    /**
     * Get all alive, unregistered human and ai id
     */
    suspend fun getAvailableIdList(): List<Int> {
        mutex.withLock {
            return if (isWaiting()) {
                availableIdList.filter { !humanIdPasswordMap.keys.contains(it) }
            } else {
                listOf()
            }
        }
    }

    /**
     * Get all alive and unregistered human id
     */
    suspend fun getAvailableHumanIdList(): List<Int> {
        mutex.withLock {
            return if (isWaiting()) {
                availableHumanIdList.filter { !humanIdPasswordMap.keys.contains(it) }
            } else {
                listOf()
            }
        }
    }

    /**
     * Get dead id list
     */
    suspend fun isPlayerDead(checkIsPlayerDeadMessage: CheckIsPlayerDeadMessage): Boolean {
        mutex.withLock {
            return if (isWaiting() &&
                humanIdPasswordMap.keys.contains(checkIsPlayerDeadMessage.id) &&
                humanIdPasswordMap.getValue(checkIsPlayerDeadMessage.id) == checkIsPlayerDeadMessage.password
            ) {
                deadIdList.contains(checkIsPlayerDeadMessage.id)
            } else {
                false
            }
        }
    }

    /**
     * Get universe 3D view for player
     *
     * @param universeData3DMessage contain the id of the player getting the view
     */
    suspend fun getUniverseData3D(universeData3DMessage: UniverseData3DMessage): UniverseData3DAtPlayer {
        mutex.withLock {
            return if (
                isWaiting() &&
                availableHumanIdList.contains(universeData3DMessage.id) &&
                humanIdPasswordMap.keys.contains(universeData3DMessage.id) &&
                humanIdPasswordMap.getValue(universeData3DMessage.id) == universeData3DMessage.password
            ) {
                universe.getUniverse3DViewAtPlayer(universeData3DMessage.id)
            } else {
                logger.error("Server: getUniverseData3D error")
                // Empty 3D view
                UniverseData3DAtPlayer()
            }
        }
    }

    /**
     * Register human player to humanIdPasswordMap
     *
     * @return success or not
     */
    suspend fun registerPlayer(registerPlayerMessage: RegisterPlayerMessage): Boolean {
        mutex.withLock {
            return if (isWaiting() && !humanIdPasswordMap.containsKey(registerPlayerMessage.id)) {
                humanIdPasswordMap[registerPlayerMessage.id] = registerPlayerMessage.password
                availableHumanIdList.add(registerPlayerMessage.id)
                logger.debug("Register player ${registerPlayerMessage.id}")
                true
            } else {
                false
            }
        }
    }


    /**
     * Deregister human player from humanIdPasswordMap
     *
     * @return success or not
     */
    suspend fun deregisterPlayer(deregisterPlayerMessage: DeregisterPlayerMessage): Boolean {
        mutex.withLock {
            return if (isWaiting() && humanIdPasswordMap.containsKey(deregisterPlayerMessage.id)) {
                val isPasswordCorrect: Boolean = humanIdPasswordMap[deregisterPlayerMessage.id] ==
                        deregisterPlayerMessage.password
                if (isPasswordCorrect) {
                    humanIdPasswordMap.remove(deregisterPlayerMessage.id)
                    availableHumanIdList.remove(deregisterPlayerMessage.id)
                    logger.debug("Deregister player ${deregisterPlayerMessage.id}")
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
    }

    /**
     * Run universe, compute the ai command in the first turn without entering the main loop
     */
    suspend fun runUniverse() {
        mutex.withLock {
            // Skip universe process in the first round
            isProcessDone.set(true)

            // Don't update this to prevent clearing registered player, this has already been run when setUniverse
            //updateCommandMapAndIdList()

            isServerWaitingInput.set(true)
            isUniverseRunning.set(true)
        }
    }

    /**
     * Stop universe
     */
    suspend fun stopUniverse() {
        mutex.withLock {
            isUniverseRunning.set(false)
            isServerWaitingInput.set(false)
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger("UniverseServerInternal")
    }
}