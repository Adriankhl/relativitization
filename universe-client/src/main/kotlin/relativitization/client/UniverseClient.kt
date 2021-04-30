package relativitization.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.observer.*
import io.ktor.client.statement.*
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.serializer.DataSerializer

class UniverseClient {
    val ktorClient = HttpClient(CIO) {
        install(HttpTimeout)
        install(JsonFeature) {
            serializer = KotlinxSerializer(DataSerializer.format)
        }
    }

    var password: String = "player password"

    var universeData3DCache: UniverseData3DAtPlayer = UniverseData3DAtPlayer()
}