package relativitization.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import relativitization.universe.UniverseClientSettings
import relativitization.universe.UniverseServerSettings
import relativitization.universe.communication.*
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.DummyCommand
import relativitization.universe.data.physics.Int3D
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.utils.CoroutineBoolean
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.properties.Delegates

/**
 * @property universeClientSettings settings of the client, should only be updated by setUniverseClientSettings()
 */
class UniverseClient(var universeClientSettings: UniverseClientSettings) {
    // The main mutex for serverStatus change
    private val mutex: Mutex = Mutex()

    // Specifically for data3D Map, should prevent calling other observable withing the lock to prevent dead lock
    private val universeData3DMapMutex: Mutex = Mutex()

    val ktorClient = HttpClient(CIO) {
        install(HttpTimeout)
        install(JsonFeature) {
            serializer = KotlinxSerializer(DataSerializer.format)
        }
    }

    private var universeClientRunJob: Job = Job()

    // for generate universe
    var generateSettings: GenerateSettings = GenerateSettings.loadOrDefault()

    // For changing server setting
    var universeServerSettings: UniverseServerSettings = UniverseServerSettings(
        adminPassword = universeClientSettings.adminPassword
    )

    // Server status, use default universe name from server setting
    // Set private because this should be updated with mutex
    private val onServerStatusChangeFunctionList: MutableList<() -> Unit> = mutableListOf()
    private var serverStatus: UniverseServerStatusMessage by Delegates.observable(
        UniverseServerStatusMessage(UniverseSettings().universeName)
    ) { _, _, _ ->
        onServerStatusChangeFunctionList.forEach { it() }
    }

    fun getCurrentServerStatus() = serverStatus


    // store downloaded but not yet used universe data
    private var universeData3DCache: UniverseData3DAtPlayer = UniverseData3DAtPlayer()

    // is new universe data ready
    val isNewDataReady: CoroutineBoolean = CoroutineBoolean(false)

    // Store map of universe data from description to data
    private val universeData3DMap: MutableMap<String, UniverseData3DAtPlayer> = mutableMapOf()

    // Current universe data 3d
    val onUniverseData3DChangeFunctionList: MutableList<() -> Unit> = mutableListOf()
    private var currentUniverseData3DAtPlayer: UniverseData3DAtPlayer by Delegates.observable(
        UniverseData3DAtPlayer()
    ) { _, _, _ ->
        onUniverseData3DChangeFunctionList.forEach { it() }
    }

    // universe view int3D and z limit
    val onUniverseDataViewChangeFunctionList: MutableList<() -> Unit> = mutableListOf()
    val changeUniverseDataView: () -> Unit = {
        universeClientSettings.viewCenter.x = primarySelectedInt3D.x
        universeClientSettings.viewCenter.y = primarySelectedInt3D.y
        universeClientSettings.viewCenter.z = primarySelectedInt3D.z
        onUniverseDataViewChangeFunctionList.forEach { it() }
    }

    // Primary selected int3D
    val onPrimarySelectedInt3DChangeFunctionList: MutableList<() -> Unit> = mutableListOf()
    var primarySelectedInt3D: Int3D by Delegates.observable(Int3D(0, 0, 0)) { _, _, _ ->
        onPrimarySelectedInt3DChangeFunctionList.forEach { it() }
    }

    // Primary selected player id
    val onPrimarySelectedPlayerIdChangeFunctionList: MutableList<() -> Unit> = mutableListOf()
    var primarySelectedPlayerId: Int by Delegates.observable(currentUniverseData3DAtPlayer.id) { _, _, _ ->
        onPrimarySelectedPlayerIdChangeFunctionList.forEach { it() }
    }

    // All selected player id
    val onSelectedPlayerIdListChangeFunctionList: MutableList<() -> Unit> = mutableListOf()
    var selectedPlayerIdList: MutableList<Int> = mutableListOf()
    var newSelectedPlayerId: Int by Delegates.observable(currentUniverseData3DAtPlayer.id) { _, _, newValue ->
        if (!selectedPlayerIdList.contains(newValue)) {
            if (!selectedPlayerIdList.contains(primarySelectedPlayerId)) {
                // Change primary selected player id if it is not stored in the all selected player id list
                primarySelectedPlayerId = newValue
            }
            selectedPlayerIdList.add(newValue)
        } else {
            // Remove selected player id if it has already been selected
            selectedPlayerIdList.remove(newValue)
        }
        onSelectedPlayerIdListChangeFunctionList.forEach { it() }
    }

    // For scrolling the map to focus on this player id
    val onMapCenterPlayerIdChangeFunctionList: MutableList<() -> Unit> = mutableListOf()
    var mapCenterPlayerId: Int by Delegates.observable(currentUniverseData3DAtPlayer.id) { _, _, _ ->
        onMapCenterPlayerIdChangeFunctionList.forEach { it() }
    }


    // store list of command for sending them to the universe server
    val commandList: MutableList<Command> = mutableListOf()

    // command showing on GUI, can be new command to be confirmed or old command to be cancelled
    val onCurrentCommandChangeFunctionList: MutableList<() -> Unit> = mutableListOf()
    var currentCommand: Command by Delegates.observable(DummyCommand()) { _, _, _ ->
        onCurrentCommandChangeFunctionList.forEach { it() }
    }

    // is this player dead
    val onIsPlayerDeadChangeFunctionList: MutableList<() -> Unit> = mutableListOf()
    var isPlayerDead: Boolean by Delegates.observable(false) { _, _, _ ->
        onIsPlayerDeadChangeFunctionList.forEach { it() }
    }

    /**
     * Start auto updating status and universeData3DCache
     */
    private suspend fun run() = coroutineScope {
        while (isActive) {
            logger.debug("Client running")
            delay(2000)

            // Modify serverStatus and universeData3DMap, mutex and universeData3DMutex should take care of this
            mutex.withLock {
                val newServerStatus = httpGetUniverseServerStatus()
                if (shouldUpdateCache(newServerStatus)) {
                    logger.debug("Going to update cache")
                    val universeData3DDownloaded = httpGetUniverseData3D()
                    // id == -1 means the data is invalid
                    if (universeData3DDownloaded.id != -1) {
                        universeData3DCache = universeData3DDownloaded
                        updateUniverseData3DMap()
                    } else {
                        isPlayerDead = httpGetCheckIsPlayerDead()

                        if (!isPlayerDead) {
                            logger.error("run(): Can't get universe")
                        }
                    }
                }
                // Trigger onServerStatusChangeFunctions
                serverStatus = newServerStatus
            }
        }
    }

    /**
     * Whether the client should update the universe data cache
     */
    private fun shouldUpdateCache(universeServerStatusMessage: UniverseServerStatusMessage): Boolean {
        val differentName =
            universeServerStatusMessage.universeName != universeData3DCache.universeSettings.universeName
        val differentTime = universeServerStatusMessage.currentUniverseTime != universeData3DCache.center.t
        return (universeServerStatusMessage.success &&
                universeServerStatusMessage.isUniverseRunning &&
                universeServerStatusMessage.hasUniverse &&
                universeServerStatusMessage.isServerWaitingInput &&
                (differentName || differentTime))
    }

    /**
     * Start running and store job
     */
    suspend fun start() = coroutineScope {
        universeClientRunJob = launch {
            run()
        }
    }

    /**
     * Stop the client
     */
    suspend fun stop() {
        clear()
        universeClientRunJob.cancelAndJoin()
        ktorClient.close()
    }

    /**
     * Clear selected player and int3D, use default value
     */
    fun clearSelected() {
        selectedPlayerIdList.clear()
        // Change selected player id twice to unselect primary player
        newSelectedPlayerId = getUniverseData3D().id
        newSelectedPlayerId = getUniverseData3D().id
        primarySelectedInt3D = getUniverseData3D().get(getUniverseData3D().id).int4D.toInt3D()
    }

    /**
     * Clear command list
     */
    fun clearCommandList() {
        commandList.clear()
        currentCommand = DummyCommand()
    }

    /**
     * Clear all on chang function list
     */
    suspend fun clearOnChangeFunctionList() {
        // use mutex since it affect the run loop
        mutex.withLock {
            onServerStatusChangeFunctionList.clear()
        }
        onPrimarySelectedInt3DChangeFunctionList.clear()
        onCurrentCommandChangeFunctionList.clear()
        onPrimarySelectedPlayerIdChangeFunctionList.clear()
        onSelectedPlayerIdListChangeFunctionList.clear()
        onUniverseData3DChangeFunctionList.clear()
        onUniverseDataViewChangeFunctionList.clear()
    }

    /**
     * Clear the client
     */
    suspend fun clear() {
        // Avoid conflicting with the main run loop when clearing universeData3DMap
        clearCommandList()
        clearOnChangeFunctionList()
        universeData3DMapMutex.withLock {
            universeData3DMap.clear()
        }
        generateSettings = GenerateSettings()
    }

    /**
     * Generate name for new universe data to store in universeData3DMap recursively
     */
    private fun universeData3DName(
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        nameList: List<String>,
        iterateNum: Int = 0
    ): String {
        val originalName: String = universeData3DAtPlayer.universeSettings.universeName +
                " - " + universeData3DAtPlayer.center.t

        val modifiedName: String = if (iterateNum == 0) {
            originalName
        } else {
            "$originalName ($iterateNum)"
        }

        return if (nameList.contains(modifiedName)) {
            universeData3DName(universeData3DAtPlayer, nameList, iterateNum + 1)
        } else {
            modifiedName
        }
    }

    /**
     * Add data cache to universeData3DMap
     */
    private suspend fun updateUniverseData3DMap() {
        universeData3DMapMutex.withLock {
            val name: String = universeData3DName(universeData3DCache, universeData3DMap.keys.toList())
            universeData3DMap[name] = universeData3DCache
            isNewDataReady.set(true)
        }
    }

    /**
     * Update current UniverseData3DTime to latest time available from universeData3DMap
     */
    suspend fun pickLatestUniverseData3D() {
        val currentData = universeData3DMapMutex.withLock {
            if (universeData3DMap.isNotEmpty()) {
                universeData3DMap.values.last()
            } else {
                logger.error("Empty universe data map")
                currentUniverseData3DAtPlayer
            }
        }
        // set the data outside of the lock to prevent dead lock
        currentUniverseData3DAtPlayer = currentData
        isNewDataReady.set(false)
    }

    /**
     * Goto stored previous universe data
     */
    suspend fun previousUniverseData3D() {
        val currentData = universeData3DMapMutex.withLock {
            if (universeData3DMap.values.contains(currentUniverseData3DAtPlayer)) {
                val currentIndex = universeData3DMap.values.indexOf(currentUniverseData3DAtPlayer)
                if (universeData3DMap.values.indices.contains(currentIndex - 1)) {
                    universeData3DMap.values.elementAt(currentIndex - 1)
                } else {
                    logger.debug("No previous data")
                    currentUniverseData3DAtPlayer
                }
            } else {
                logger.error("No data 3D index")
                currentUniverseData3DAtPlayer
            }
        }
        // set this outside of the lock to prevent dead lock
        currentUniverseData3DAtPlayer = currentData
    }

    /**
     * Goto stored next universe data
     */
    suspend fun nextUniverseData3D() {
        val currentData = universeData3DMapMutex.withLock {
            if (universeData3DMap.values.contains(currentUniverseData3DAtPlayer)) {
                val currentIndex = universeData3DMap.values.indexOf(currentUniverseData3DAtPlayer)
                if (universeData3DMap.values.indices.contains(currentIndex + 1)) {
                    universeData3DMap.values.elementAt(currentIndex + 1)
                } else {
                    logger.debug("No next data")
                    currentUniverseData3DAtPlayer
                }
            } else {
                logger.error("No data 3D index")
                currentUniverseData3DAtPlayer
            }
        }
        // set this outside of lock to prevent deadlock
        currentUniverseData3DAtPlayer = currentData
    }


    /**
     * Pick universe data from map
     */
    suspend fun pickUniverseData3D(name: String) {
        val currentData = universeData3DMapMutex.withLock {
            if (universeData3DMap.keys.contains(name)) {
                universeData3DMap.getValue(name)
            } else {
                logger.error("Picking non existing universeData")
                currentUniverseData3DAtPlayer
            }
        }
        // Set this outside of lock to prevent dead lock
        currentUniverseData3DAtPlayer = currentData
    }


    /**
     * Get all available time from map
     */
    suspend fun getAvailableData3DName(): List<String> {
        universeData3DMapMutex.withLock {
            return universeData3DMap.keys.toList()
        }
    }

    /**
     * Get the key name of the current universe data
     */
    suspend fun getCurrentData3DName(): String {
        universeData3DMapMutex.withLock {
            return universeData3DMap.keys.elementAt(universeData3DMap.values.indexOf(currentUniverseData3DAtPlayer))
        }
    }

    /**
     * Clear old data3D
     */
    suspend fun clearOldData3D() {
        val currentName = getCurrentData3DName()
        universeData3DMapMutex.withLock {
            val index: Int = universeData3DMap.keys.indexOf(currentName)
            val removeKeyList: List<String> = universeData3DMap.keys.take(index)
            universeData3DMap.keys.removeAll { removeKeyList.contains(it) }
        }
        // trigger data change
        currentUniverseData3DAtPlayer = currentUniverseData3DAtPlayer
    }

    /**
     * Whether the universe data contains primary player id
     */
    fun isPrimarySelectedPlayerIdValid(): Boolean {
        return getUniverseData3D().playerDataMap.keys.contains(primarySelectedPlayerId)
    }

    /**
     * Confirm commandToBeConfirm
     */
    fun confirmCurrentCommand() {
        if (!isCurrentCommandStored()) {
            commandList.add(currentCommand)
            currentCommand = if (commandList.isEmpty()) {
                DummyCommand()
            } else {
                commandList.last()
            }
        } else {
            logger.error("Trying to confirm existing command")
        }
    }

    /**
     * Cancel commandToBeConfirm
     */
    fun cancelCurrentCommand() {
        if (isCurrentCommandStored()) {
            val index = commandList.indexOf(currentCommand)
            commandList.remove(currentCommand)
            currentCommand = when {
                commandList.isEmpty() -> {
                    DummyCommand()
                }
                index < commandList.size - 1 -> {
                    commandList[index]
                }
                else -> {
                    commandList.last()
                }
            }
        } else {
            logger.error("Trying to cancel non-existing command")
        }
    }

    /**
     * Is the current command stored in the command list
     */
    fun isCurrentCommandStored(): Boolean {
        return commandList.contains(currentCommand)
    }

    /**
     * Change to previous command
     */
    fun previousCommand() {
        if (isCurrentCommandStored()) {
            val index = commandList.indexOf(currentCommand)
            if (index > 0) {
                currentCommand = commandList[index - 1]
            } else {
                logger.debug("Can't goto previous command, already the earliest one")
            }
        } else {
            if (commandList.isNotEmpty()) {
                currentCommand = commandList.last()
            } else {
                logger.debug("Can't goto previous command, the command list is empty")
            }
        }
    }

    /**
     * Change to next command
     */
    fun nextCommand() {
        if (isCurrentCommandStored()) {
            val index = commandList.indexOf(currentCommand)
            if (index < commandList.size - 1) {
                currentCommand = commandList[index + 1]
            } else {
                logger.debug("Can't goto next command, already the latest one")
            }
        } else {
            logger.debug("Can't goto next command, the current command is not stored")
        }
    }

    /**
     * Get universe data 3D at current time
     */
    fun getUniverseData3D(): UniverseData3DAtPlayer {
        return currentUniverseData3DAtPlayer
    }


    suspend fun setUniverseClientSettings(newUniverseClientSettings: UniverseClientSettings) {
        mutex.withLock {
            universeClientSettings = newUniverseClientSettings
        }
    }

    suspend fun addToOnServerStatusChangeFunctionList(function: () -> Unit) {
        mutex.withLock {
            onServerStatusChangeFunctionList.add(function)
        }
    }

    suspend fun httpGetUniverseServerStatus(): UniverseServerStatusMessage {
        return try {
            val serverAddress = universeClientSettings.serverAddress
            val serverPort = universeClientSettings.serverPort
            ktorClient.get<UniverseServerStatusMessage>("http://$serverAddress:$serverPort/status") {
                timeout {
                    requestTimeoutMillis = 1000
                }
            }
        } catch (cause: Throwable) {
            // Server status with default universe name to prevent update
            UniverseServerStatusMessage(UniverseSettings().universeName)
        }
    }

    suspend fun httpGetAvailableIdList(): List<Int> {
        return try {
            val serverAddress = universeClientSettings.serverAddress
            val serverPort = universeClientSettings.serverPort
            ktorClient.get<List<Int>>("http://$serverAddress:$serverPort/status/ids") {
                timeout {
                    requestTimeoutMillis = 1000
                }
            }
        } catch (cause: Throwable) {
            listOf()
        }
    }

    suspend fun httpGetAvailableHumanIdList(): List<Int> {
        return try {
            val serverAddress = universeClientSettings.serverAddress
            val serverPort = universeClientSettings.serverPort
            ktorClient.get<List<Int>>("http://$serverAddress:$serverPort/status/human-ids") {
                timeout {
                    requestTimeoutMillis = 1000
                }
            }
        } catch (cause: Throwable) {
            listOf()
        }
    }


    suspend fun httpGetSavedUniverse(): List<String> {
        return try {
            val serverAddress = universeClientSettings.serverAddress
            val serverPort = universeClientSettings.serverPort
            ktorClient.get<List<String>>("http://$serverAddress:$serverPort/create/list-saved") {
                timeout {
                    requestTimeoutMillis = 1000
                }
            }
        } catch (cause: Throwable) {
            listOf()
        }
    }

    suspend fun httpGetCheckIsPlayerDead(): Boolean {
        return try {
            val serverAddress = universeClientSettings.serverAddress
            val serverPort = universeClientSettings.serverPort
            val playerId = universeClientSettings.playerId
            val password = universeClientSettings.password
            ktorClient.get<Boolean>("http://$serverAddress:$serverPort/run/dead") {
                timeout {
                    contentType(ContentType.Application.Json)
                    body = CheckIsPlayerDeadMessage(playerId, password)
                    requestTimeoutMillis = 1000
                }
            }
        } catch (cause: Throwable) {
            false
        }
    }

    suspend fun httpGetUniverseData3D(): UniverseData3DAtPlayer {
        return try {
            val serverAddress = universeClientSettings.serverAddress
            val serverPort = universeClientSettings.serverPort
            val playerId = universeClientSettings.playerId
            val password = universeClientSettings.password
            ktorClient.get<UniverseData3DAtPlayer>("http://$serverAddress:$serverPort/run/view") {
                contentType(ContentType.Application.Json)
                body = UniverseData3DMessage(playerId, password)
                timeout {
                    requestTimeoutMillis = 10000
                }
            }
        } catch (cause: Throwable) {
            logger.error("httpGetUniverseData3D error, returning empty data")
            UniverseData3DAtPlayer()
        }
    }

    suspend fun httpPostUniverseServerSettings(): HttpStatusCode {
        return try {
            val serverAddress = universeClientSettings.serverAddress
            val serverPort = universeClientSettings.serverPort
            val adminPassword = universeClientSettings.adminPassword
            val response: HttpResponse =
                ktorClient.post("http://$serverAddress:$serverPort/run/update-server-settings") {
                    contentType(ContentType.Application.Json)
                    body = UniverseServerSettingsMessage(adminPassword, universeServerSettings)
                    timeout {
                        requestTimeoutMillis = 1000
                    }
                }
            logger.debug("Update universe settings status: ${response.status}")
            response.status
        } catch (cause: ResponseException) {
            logger.error("httpPostUniverseServerSettings error: " + cause.response.status)
            cause.response.status
        } catch (cause: Throwable) {
            logger.error("postNewUniverse error: cannot find server")
            HttpStatusCode.NotFound
        }
    }

    suspend fun httpPostNewUniverse(): HttpStatusCode {
        return try {
            val serverAddress = universeClientSettings.serverAddress
            val serverPort = universeClientSettings.serverPort
            val adminPassword = universeClientSettings.adminPassword
            val response: HttpResponse = ktorClient.post("http://$serverAddress:$serverPort/create/new") {
                contentType(ContentType.Application.Json)
                body = NewUniverseMessage(adminPassword, generateSettings)
                timeout {
                    requestTimeoutMillis = 1000
                }
            }
            logger.debug("Create new universe status: ${response.status}")
            response.status
        } catch (cause: ResponseException) {
            logger.error("postNewUniverse error: " + cause.response.status)
            cause.response.status
        } catch (cause: Throwable) {
            logger.error("postNewUniverse error: cannot find server")
            HttpStatusCode.NotFound
        }
    }

    suspend fun httpPostLoadUniverse(universeName: String): HttpStatusCode {
        return try {
            val serverAddress = universeClientSettings.serverAddress
            val serverPort = universeClientSettings.serverPort
            val adminPassword = universeClientSettings.adminPassword
            val response: HttpResponse = ktorClient.post("http://$serverAddress:$serverPort/create/load") {
                contentType(ContentType.Application.Json)
                body = LoadUniverseMessage(adminPassword, universeName)
                timeout {
                    requestTimeoutMillis = 1000
                }
            }

            logger.debug("Create new universe status: ${response.status}")
            response.status
        } catch (cause: ResponseException) {
            logger.error("postLoadUniverse error: " + cause.response.status)
            cause.response.status
        } catch (cause: Throwable) {
            logger.error("postLoadUniverse error: cannot find server")
            HttpStatusCode.NotFound
        }
    }

    suspend fun httpPostRegisterPlayer(): HttpStatusCode {
        return try {
            val serverAddress = universeClientSettings.serverAddress
            val serverPort = universeClientSettings.serverPort
            val playerId = universeClientSettings.playerId
            val password = universeClientSettings.password
            val response: HttpResponse = ktorClient.post("http://$serverAddress:$serverPort/run/register") {
                contentType(ContentType.Application.Json)
                body = RegisterPlayerMessage(playerId, password)
                timeout {
                    requestTimeoutMillis = 1000
                }
            }

            logger.debug("Register player universe status: ${response.status}")
            response.status
        } catch (cause: ResponseException) {
            logger.error("Register player error: " + cause.response.status)
            cause.response.status
        } catch (cause: Throwable) {
            logger.error("Register player error: cannot find server")
            HttpStatusCode.NotFound
        }
    }

    suspend fun httpPostRunUniverse(): HttpStatusCode {
        return try {
            val serverAddress = universeClientSettings.serverAddress
            val serverPort = universeClientSettings.serverPort
            val adminPassword = universeClientSettings.adminPassword
            val response: HttpResponse = ktorClient.post("http://$serverAddress:$serverPort/run/universe-run") {
                contentType(ContentType.Application.Json)
                body = RunUniverseMessage(adminPassword)
                timeout {
                    requestTimeoutMillis = 1000
                }
            }
            logger.debug("Run universe: ${response.status}")
            response.status
        } catch (cause: ResponseException) {
            logger.error("Run universe error: " + cause.response.status)
            cause.response.status
        } catch (cause: Throwable) {
            logger.error("Run universe error: cannot find server")
            HttpStatusCode.NotFound
        }
    }


    suspend fun httpPostStopUniverse(): HttpStatusCode {
        return try {
            val serverAddress = universeClientSettings.serverAddress
            val serverPort = universeClientSettings.serverPort
            val adminPassword = universeClientSettings.adminPassword
            val response: HttpResponse = ktorClient.post("http://$serverAddress:$serverPort/run/universe-stop") {
                contentType(ContentType.Application.Json)
                body = StopUniverseMessage(adminPassword)
                timeout {
                    requestTimeoutMillis = 1000
                }
            }
            logger.debug("Stop universe: ${response.status}")
            response.status
        } catch (cause: ResponseException) {
            logger.error("Stop universe error: " + cause.response.status)
            cause.response.status
        } catch (cause: Throwable) {
            logger.error("Stop universe error: cannot find server")
            HttpStatusCode.NotFound
        }
    }


    suspend fun httpPostHumanInput(): HttpStatusCode {
        return try {
            val serverAddress = universeClientSettings.serverAddress
            val serverPort = universeClientSettings.serverPort
            val playerId = universeClientSettings.playerId
            val password = universeClientSettings.password
            val response: HttpResponse = ktorClient.post("http://$serverAddress:$serverPort/run/input") {
                contentType(ContentType.Application.Json)
                body = CommandInputMessage(playerId, password, commandList.toList())
                timeout {
                    requestTimeoutMillis = 1000
                }
            }
            logger.debug("Human command input: ${response.status}")
            response.status
        } catch (cause: ResponseException) {
            logger.error("Human command input error: " + cause.response.status)
            cause.response.status
        } catch (cause: Throwable) {
            logger.error("Human command input error: cannot find server")
            HttpStatusCode.NotFound
        }
    }

    suspend fun httpPostStopWaiting(): HttpStatusCode {
        return try {
            val serverAddress = universeClientSettings.serverAddress
            val serverPort = universeClientSettings.serverPort
            val adminPassword = universeClientSettings.adminPassword
            val response: HttpResponse = ktorClient.post("http://$serverAddress:$serverPort/run/stop-waiting") {
                contentType(ContentType.Application.Json)
                body = StopWaitingMessage(adminPassword)
                timeout {
                    requestTimeoutMillis = 1000
                }
            }
            logger.debug("Stop waiting: ${response.status}")
            response.status
        } catch (cause: ResponseException) {
            logger.error("Stop waiting error: " + cause.response.status)
            cause.response.status
        } catch (cause: Throwable) {
            logger.error("Stop waiting error: cannot find server")
            HttpStatusCode.NotFound
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}