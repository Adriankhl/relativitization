package relativitization.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import relativitization.universe.data.UniverseData3DAtPlayer

class UniverseClient {
    val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 1000
        }
    }
    var UniverseData3DCache: UniverseData3DAtPlayer = UniverseData3DAtPlayer()
}