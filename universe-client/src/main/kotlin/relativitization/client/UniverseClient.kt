package relativitization.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*

class UniverseClient {
    val client = HttpClient(CIO)
}