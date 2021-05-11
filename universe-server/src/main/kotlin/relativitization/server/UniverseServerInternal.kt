package relativitization.server

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.apache.logging.log4j.LogManager
import relativitization.universe.Universe
import relativitization.universe.UniverseServerSettings
import relativitization.universe.communication.CommandInputMessage
import relativitization.universe.communication.RegisterPlayerMessage
import relativitization.universe.communication.UniverseServerStatusMessage
import relativitization.universe.communication.UniverseData3DMessage
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.generate.GenerateSetting
import relativitization.universe.generate.GenerateUniverse
import relativitization.universe.utils.CoroutineBoolean
import relativitization.universe.utils.CoroutineVar

class UniverseServerInternal(val universeServerSettings: UniverseServerSettings) {
    private val mutex: Mutex = Mutex()

    // Data of universe
    private var universe: Universe = Universe(GenerateUniverse.generate(GenerateSetting()))

    // Current universe time
    private var currentUniverseTime: Int = 0

    // Whether there is already a universe
    private var hasUniverse: CoroutineBoolean = CoroutineBoolean(false)

    // Whether the universe is running
    private var runningUniverse: CoroutineBoolean = CoroutineBoolean(false)

    // is waiting input from human
    // client can only get data and post command list if this is true
    private var waitingInput: CoroutineBoolean = CoroutineBoolean(false)

    // wait beginning time in milli second, used to calculate the time limit to stop waiting
    private var waitBeginTime: CoroutineVar<Long> = CoroutineVar(System.currentTimeMillis())

    // map from registered player id to password
    private val humanIdPasswordMap: MutableMap<Int, String> = mutableMapOf()

    // Available id list
    private val availableIdList: MutableList<Int> = mutableListOf()

    // Available (suggested) human id list
    private val availableHumanIdList: MutableList<Int> = mutableListOf()

    // command Map for human input
    private val humanCommandMap: MutableMap<Int, List<Command>> = mutableMapOf()

    // ai computed command map
    private val aiCommandMap: MutableMap<Int, List<Command>> = mutableMapOf()

    /**
     * Start the universe
     */
    suspend fun start() = coroutineScope {
        while (isActive) {
            delay(1000)

            if (runningUniverse.isTrue()) {
                mutex.withLock {
                    if (allHumanInputReady() || (!waitingInput.isTrue()) || exceedTimeLimit()) {
                        waitingInput.set(false)
                        setTimeLeftTo(0)
                        logger.debug("Not accepting new input")
                    }
                }

                if (!waitingInput.isTrue()) {
                    // Post-process then pre-process since the universe accept input in the middle of game turn
                    universe.postProcessUniverse(humanCommandMap, aiCommandMap)
                    universe.preprocessUniverse()

                    // Clear and update the command maps and player id list
                    updateCommandMapAndIdList()

                    // Update current universe time
                    currentUniverseTime = universe.getCurrentUniverseTime()

                    // Clear inactive (no input received) player
                    if (universeServerSettings.getCleanInactivePerTurn()) {
                        clearInactive()
                    }

                    // Start waiting for human input and compute ai input
                    humanAndAiInput()
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
        runningUniverse.set(false)
        job.cancelAndJoin()
    }

    /**
     * Is waiting for input or waiting to start
     */
    private suspend fun isWaiting(): Boolean {
        return ((hasUniverse.isTrue() && !runningUniverse.isTrue()) ||
                (runningUniverse.isTrue() && waitingInput.isTrue())
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

        // Change available id
        availableIdList.addAll(universe.availablePlayers())
        availableHumanIdList.addAll(universe.availableHumanPLayers())
    }

    /**
     * Start parallel computation of ai input and accept human input
     */
    private suspend fun humanAndAiInput() {
        // Start to accept human input
        waitingInput.set(true)
        logger.debug("Start accepting new input")

        aiCommandMap.putAll(universe.computeAICommands())

        // Restart wait timer after ai command has been computed
        setTimeLeftTo(universeServerSettings.getWaitTimeLimit())

        logger.debug("AI done computation")
    }

    /**
     * Clear inactive player registration: id and password
     */
    private fun clearInactive() {
        val oldIdList = humanIdPasswordMap.keys.toList()
        val toRemoveIdList = oldIdList.filter { id -> !availableHumanIdList.contains(id) }
        for (id in toRemoveIdList) {
            humanIdPasswordMap.remove(id)
        }
    }

    /**
     * Time left for waiting, can be negative
     */
    private suspend fun timeLeft(): Long =
        (universeServerSettings.getWaitTimeLimit() * 1000).toLong() - (System.currentTimeMillis() - waitBeginTime.get())


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
    private suspend fun setTimeLeftTo(time: Int) {
        waitBeginTime.set(
            (universeServerSettings.getWaitTimeLimit() * 1000).toLong() + System.currentTimeMillis() - (time * 1000).toLong()
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
    suspend fun humanInput(commandInputMessage: CommandInputMessage): Boolean {
        mutex.withLock {
            return if (
                (humanIdPasswordMap.keys.contains(commandInputMessage.id)) &&
                (humanIdPasswordMap.getValue(commandInputMessage.id) == commandInputMessage.password) &&
                (waitingInput.isTrue())
            ) {
                humanCommandMap[commandInputMessage.id] = commandInputMessage.commandList
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
        mutex.withLock {
            if (!runningUniverse.isTrue()) {
                universe = newUniverse
                updateCommandMapAndIdList()
                currentUniverseTime = universe.getCurrentUniverseTime()
                hasUniverse.set(true)
            }
        }
    }

    /**
     * Get universe status
     */
    suspend fun getUniverseStatusMessage(): UniverseServerStatusMessage {
        mutex.withLock {
            return UniverseServerStatusMessage(
                success = true,
                hasUniverse = hasUniverse.isTrue(),
                runningUniverse = runningUniverse.isTrue(),
                waitingInput = waitingInput.isTrue(),
                timeLeft = timeLeft(),
                currentUniverseTime = currentUniverseTime
            )
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
     * Get universe 3D view for player
     *
     * @param playerId the id of the player getting the view
     */
    suspend fun getUniverseData3D(universeData3DMessage: UniverseData3DMessage): UniverseData3DAtPlayer {
        mutex.withLock {
            return if (
                isWaiting() &&
                universe.availableHumanPLayers().contains(universeData3DMessage.id) &&
                humanIdPasswordMap.keys.contains(universeData3DMessage.id) &&
                humanIdPasswordMap.getValue(universeData3DMessage.id) == universeData3DMessage.password
            ) {
                universe.getUniverse3DViewAtPlayer(universeData3DMessage.id)
            } else {
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
            return if (isWaiting() && !humanIdPasswordMap.keys.contains(registerPlayerMessage.id)) {
                humanIdPasswordMap[registerPlayerMessage.id] = registerPlayerMessage.password
                true
            } else {
                false
            }
        }
    }

    /**
     * Run universe, compute the ai command in the first turn without entering the main loop
     */
    suspend fun runUniverse()  {
        mutex.withLock {
            updateCommandMapAndIdList()
            waitingInput.set(true)
            runningUniverse.set(true)
        }
        humanAndAiInput()
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}