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
import org.apache.logging.log4j.LogManager
import relativitization.universe.UniverseClientSettings
import relativitization.universe.UniverseServerSettings
import relativitization.universe.communication.*
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.GenerateSetting
import relativitization.universe.utils.CoroutineBoolean

/**
 * @property universeClientSettings settings of the client, should only be updated by setUniverseClientSettings()
 */
class UniverseClient(var universeClientSettings: UniverseClientSettings) {
    private val mutex: Mutex = Mutex()

    val ktorClient = HttpClient(CIO) {
        install(HttpTimeout)
        install(JsonFeature) {
            serializer = KotlinxSerializer(DataSerializer.format)
        }
    }

    private var universeClientRunJob: Job = Job()

    // store downloaded but not yet used universe data
    private var universeData3DCache: UniverseData3DAtPlayer = UniverseData3DAtPlayer()
    
    // is new universe data ready
    val isNewDataReady: CoroutineBoolean = CoroutineBoolean(false)

    // Current universe data 3d time
    private var currentUniverseData3DAtPlayer: UniverseData3DAtPlayer = UniverseData3DAtPlayer()

    // Store map of universe data from description to data
    private val universeData3DMap: MutableMap<String, UniverseData3DAtPlayer> = mutableMapOf()

    // input command list
    val commandList: MutableList<Command> = mutableListOf()

    // for generate universe
    var generateSettings: GenerateSetting = GenerateSetting()

    // For changing server setting
    var universeServerSettings: UniverseServerSettings = UniverseServerSettings(
        adminPassword = universeClientSettings.adminPassword
    )

    // Server status, use default universe name from server setting
    private var serverStatus: UniverseServerStatusMessage = UniverseServerStatusMessage(UniverseSettings().universeName)

    val updatableByClient: MutableList<() -> Unit> = mutableListOf()

    // Gui related data
    var firstSelectedPlayerId: Int = -1
    var selectedPlayerIds: MutableList<Int> = mutableListOf()

    /**
     * Start auto updating status and universeData3DCache
     */
    private suspend fun run() = coroutineScope {
        while (isActive) {
            logger.debug("Client running")
            delay(2000)
            mutex.withLock {
                serverStatus = httpGetUniverseServerStatus()
                if (shouldUpdateCache(serverStatus)) {
                    logger.debug("Going to update cache")
                    val universeData3DDownloaded =  httpGetUniverseData3D()
                    // id == -1 means the data is invalid
                    if (universeData3DDownloaded.id != -1) {
                        universeData3DCache = universeData3DDownloaded
                        updateUniverseData3DMap()
                    } else {
                        logger.error("run(): Can't get universe")
                    }
                }
            }
            updatableByClient.forEach{ it() }
        }
    }

    /**
     * Whether the client should update the universe data cache
     */
    fun shouldUpdateCache(universeServerStatusMessage: UniverseServerStatusMessage): Boolean {
        val differentName = universeServerStatusMessage.universeName != universeData3DCache.universeSettings.universeName
        val differentTime = universeServerStatusMessage.currentUniverseTime != universeData3DCache.center.t
        return (universeServerStatusMessage.success &&
                universeServerStatusMessage.runningUniverse &&
                universeServerStatusMessage.hasUniverse &&
                universeServerStatusMessage.waitingInput &&
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
     * Clear the client
     */
    suspend fun clear() {
        universeData3DMap.clear()
        commandList.clear()
        generateSettings = GenerateSetting()
        updatableByClient.clear()
    }

    /**
     * Generate name for new universe data to store in universeData3DMap recursively
     */
    private fun universeData3DName(
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        iterateNum: Int = 0
    ): String {
        val originalName: String = universeData3DAtPlayer.universeSettings.universeName +
                " - " + universeData3DAtPlayer.center.t

        val modifiedName: String = if (iterateNum == 0) {
            originalName
        } else {
            "$originalName ($iterateNum)"
        }

        return if (universeData3DMap.keys.contains(modifiedName)) {
            universeData3DName(universeData3DAtPlayer, iterateNum + 1)
        } else {
            modifiedName
        }
    }

    /**
     * Add data cache to universeData3DMap
     */
    private suspend fun updateUniverseData3DMap() {
        val name: String = universeData3DName(universeData3DCache)
        universeData3DMap[name] = universeData3DCache
        isNewDataReady.set(true)
    }

    /**
     * Update current UniverseData3DTime to latest time available from universeData3DMap
     */
    suspend fun pickLatestUniverseData3D() {
        currentUniverseData3DAtPlayer = universeData3DMap.values.last()
        isNewDataReady.set(false)
    }

    /**
     * Pick universe data from map
     */
    fun pickUniverseData3D(name: String) {
        if (universeData3DMap.keys.contains(name)) {
            currentUniverseData3DAtPlayer = universeData3DMap.getValue(name)
        } else {
            logger.error("Picking non existing universeData")
        }
    }


    /**
     * Get all available time from map
     */
    fun getAvailableData3DName(): List<String> {
        return universeData3DMap.keys.toList()
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

    fun getServerStatus(): UniverseServerStatusMessage = serverStatus

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
            val response: HttpResponse = ktorClient.post("http://$serverAddress:$serverPort/run/update-server-settings") {
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
            val response: HttpResponse = ktorClient.post("http://$serverAddress:$serverPort/run/universe") {
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

    suspend fun httpPostHumanInput(): HttpStatusCode {
        return try {
            val serverAddress = universeClientSettings.serverAddress
            val serverPort = universeClientSettings.serverPort
            val playerId = universeClientSettings.playerId
            val password = universeClientSettings.password
            val response: HttpResponse = ktorClient.post("http://$serverAddress:$serverPort/run/input") {
                contentType(ContentType.Application.Json)
                body = CommandInputMessage(playerId, password, commandList)
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

    companion object {
        private val logger = LogManager.getLogger()
    }
}