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
import relativitization.universe.communication.CreateUniverseMessage
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

    suspend fun postCreateUniverse(): HttpStatusCode {
        val response: HttpResponse = ktorClient.post("http://$serverAddress:$serverPort/create") {
            contentType(ContentType.Application.Json)
            body = CreateUniverseMessage(adminPassword, generateSettings)
            timeout {
                requestTimeoutMillis = 1000
            }
        }

        logger.debug("Create universe status: ${response.status}")
        return response.status
    }



    companion object {
        private val logger = LogManager.getLogger()
    }
}