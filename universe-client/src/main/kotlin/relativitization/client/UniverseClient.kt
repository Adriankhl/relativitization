package relativitization.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import relativitization.universe.data.UniverseData3DAtPlayer

class UniverseClient {
    val client = HttpClient(CIO)
    lateinit var data3DCache: UniverseData3DAtPlayer
}