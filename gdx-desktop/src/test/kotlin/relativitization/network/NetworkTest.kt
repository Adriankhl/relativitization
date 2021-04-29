package relativitization.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import relativitization.client.UniverseClient
import relativitization.server.UniverseServer

internal class NetworkTest {
    @Test
    fun helloTest() {
        val universeServer = UniverseServer()
        val universeClient = UniverseClient()

        runBlocking {
            launch {
                universeServer.start()
            }
            println("Launched universe server")
            delay(2000)
            val response: HttpResponse = universeClient.client.get("http://127.0.0.1:29979/status/hello")
            println(response.readText())
            universeServer.stop()
        }
    }
}