package relativitization.server

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    // Whether there is already a universe
    var hasUniverse: CoroutineBoolean = CoroutineBoolean(false)

    // Whether the universe is running
    var runningUniverse: CoroutineBoolean = CoroutineBoolean(false)

    // is waiting input from human
    var waitingInput: CoroutineBoolean = CoroutineBoolean(false)

    // wait beginning time, used to calculate the time limit to stop waiting
    var waitBeginTime: CoroutineVar<Long> = CoroutineVar(System.currentTimeMillis())

    // wait time limit in mini second
    var waitTimeLimit: CoroutineVar<Long> = CoroutineVar(60000L)

    // map from registered player id to password
    val humanIdPasswordMap: MutableMap<Int, String> = mutableMapOf()

    // Available id list
    val availableIdList: MutableList<Int> = mutableListOf()

    // command Map for human input
    val humanCommandMap: MutableMap<Int, List<Command>> = mutableMapOf()

    // ai computed command map
    val aiCommandMap: MutableMap<Int, List<Command>> = mutableMapOf()

    // Is ai command computed
    var aiComputed: Boolean = false

    // Clear inactive registered player id each turn or not
    var clearInactive: Boolean = true

    /**
     * Start the universe
     */
    suspend fun start() {
        while (runningUniverse.isTrue()) {
            delay(1000)
            mutex.withLock {
                if ((!waitingInput.isTrue()) || exceedTimeLimit()) {
                    waitingInput.set(false)
                }
            }

            if (!waitingInput.isTrue()) {
                // Post-process then pre-process since the universe accept input in the middle of game turn
                universe.postProcessUniverse(humanCommandMap, aiCommandMap)
                universe.preprocessUniverse()
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
     * Whether the wait time has exceed the time limit
     */
    private suspend fun exceedTimeLimit(): Boolean {
        return (System.currentTimeMillis() - waitBeginTime.get()) > waitTimeLimit.get()
    }

    /**
     * Human input with this function
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
            hasUniverse.set(true)
        }
    }
}