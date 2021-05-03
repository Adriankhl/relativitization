package relativitization.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.apache.logging.log4j.LogManager
import relativitization.universe.communication.*
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.GenerateSetting

/**
 * @property adminPassword password to admin access to server
 */
class UniverseClient(var adminPassword: String) {
    val ktorClient = HttpClient(CIO) {
        install(HttpTimeout)
        install(JsonFeature) {
            serializer = KotlinxSerializer(DataSerializer.format)
        }
    }

    // player id
    var playerId: Int = -1

    // password for holding playerId in server
    var password: String = "player password"

    // store downloaded but not yet used universe data
    var universeData3DCache: UniverseData3DAtPlayer = UniverseData3DAtPlayer()

    // input command list
    val commandList: MutableList<Command> = mutableListOf()

    // for generate universe
    var generateSettings: GenerateSetting = GenerateSetting()

    // ip/url of server
    var serverAddress = "127.0.0.1"
    var serverPort = "29979"

    suspend fun getUniverseServerStatus(): UniverseServerStatusMessage {
        return try {
            ktorClient.get<UniverseServerStatusMessage>("http://$serverAddress:$serverPort/status") {
                timeout {
                    requestTimeoutMillis = 1000
                }
            }
        } catch (cause: Throwable) {
            UniverseServerStatusMessage()
        }
    }

    suspend fun getAvailableIdList(): List<Int> {
        return try {
            ktorClient.get<List<Int>>("http://$serverAddress:$serverPort/status/ids") {
                timeout {
                    requestTimeoutMillis = 1000
                }
            }
        } catch (cause: Throwable) {
            listOf()
        }
    }

    suspend fun getAvailableHumanIdList(): List<Int> {
        return try {
            ktorClient.get<List<Int>>("http://$serverAddress:$serverPort/status/human-ids") {
                timeout {
                    requestTimeoutMillis = 1000
                }
            }
        } catch (cause: Throwable) {
            listOf()
        }
    }

    suspend fun getUniverse3DView(): UniverseData3DAtPlayer {
        return try {
            ktorClient.get<UniverseData3DAtPlayer>("http://$serverAddress:$serverPort/run/view") {
                contentType(ContentType.Application.Json)
                body = UniverseViewMessage(playerId, password)
                timeout {
                    requestTimeoutMillis = 10000
                }
            }
        } catch (cause: Throwable) {
            UniverseData3DAtPlayer()
        }
    }

    suspend fun postNewUniverse(): HttpStatusCode {
        return try {
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

    suspend fun postLoadUniverse(universeName: String): HttpStatusCode {
        return try {
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

    suspend fun postRegisterPlayer(): HttpStatusCode {
        return try {
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

    suspend fun postRunUniverse(): HttpStatusCode {
        return try {
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

    suspend fun postHumanInput(): HttpStatusCode {
        return try {
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