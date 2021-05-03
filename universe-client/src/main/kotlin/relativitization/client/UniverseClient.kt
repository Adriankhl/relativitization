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
import relativitization.universe.communication.LoadUniverseMessage
import relativitization.universe.communication.NewUniverseMessage
import relativitization.universe.communication.UniverseServerStatusMessage
import relativitization.universe.data.UniverseData3DAtPlayer
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

    // password for holding playerId in server
    var password: String = "player password"

    // store downloaded but not yet used universe data
    var universeData3DCache: UniverseData3DAtPlayer = UniverseData3DAtPlayer()

    // for generate universe
    var generateSettings: GenerateSetting = GenerateSetting()

    // ip/url of server
    var serverAddress = "127.0.0.1"
    var serverPort = "29979"

    suspend fun getUniverseServerStatus(): UniverseServerStatusMessage {
        return try {
            ktorClient.get<UniverseServerStatusMessage>("http://$serverAddress:$serverPort/status")
        } catch (cause: Throwable) {
            UniverseServerStatusMessage()
        }
    }

    suspend fun getAvailableIdList(): List<Int> {
        return try {
            ktorClient.get<List<Int>>("http://$serverAddress:$serverPort/status/ids")
        } catch (cause: Throwable) {
            listOf()
        }
    }

    suspend fun getAvailableHumanIdList(): List<Int> {
        return try {
            ktorClient.get<List<Int>>("http://$serverAddress:$serverPort/status/human-ids")
        } catch (cause: Throwable) {
            listOf()
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


    companion object {
        private val logger = LogManager.getLogger()
    }
}