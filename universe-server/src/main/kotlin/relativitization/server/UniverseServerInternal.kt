package relativitization.server

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.apache.logging.log4j.LogManager
import relativitization.universe.Universe
import relativitization.universe.communication.CommandInputMessage
import relativitization.universe.data.commands.Command
import relativitization.universe.generate.GenerateSetting
import relativitization.universe.generate.GenerateUniverse
import relativitization.universe.utils.CoroutineBoolean
import relativitization.universe.utils.CoroutineMap
import relativitization.universe.utils.CoroutineVar

class UniverseServerInternal(var adminPassword: String) {
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

    // wait beginning time, used to calculate the time limit to stop waiting
    private var waitBeginTime: CoroutineVar<Long> = CoroutineVar(System.currentTimeMillis())

    // wait time limit in mini second
    private var waitTimeLimit: CoroutineVar<Long> = CoroutineVar(60000L)

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

    // Clear inactive registered player id each turn or not
    private var clearInactivePerTurn: CoroutineBoolean = CoroutineBoolean(true)

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
                    if (clearInactivePerTurn.isTrue()) {
                        clearInactive()
                    }

                    // Start to accept human input
                    waitingInput.set(true)
                    logger.debug("Start accepting new input")

                    aiCommandMap.putAll(universe.computeAICommands())
                    logger.debug("AI done computation")
                }
            }
        }
    }

    /**
     * Stop the universe
     */
    suspend fun stop() {
        runningUniverse.set(false)
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
     * Whether the wait time has exceed the time limit
     */
    private suspend fun exceedTimeLimit(): Boolean {
        return (System.currentTimeMillis() - waitBeginTime.get()) > waitTimeLimit.get()
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
            return if ((humanIdPasswordMap.keys.contains(commandInputMessage.id)) &&
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
            universe = newUniverse
            updateCommandMapAndIdList()
            currentUniverseTime = universe.getCurrentUniverseTime()
            hasUniverse.set(true)
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}