package relativitization.network

import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import relativitization.client.UniverseClient
import relativitization.server.UniverseServer

internal class NetworkTest {
    @Test
    fun helloTest() {
        val universeServer = UniverseServer()
        val universeClient = UniverseClient()

        runBlocking {
            launch(Dispatchers.IO) {
                universeServer.start()
            }
            println("Launched universe server")

            delay(1000)

            for (i in 1..100) {
                println("New Job")
                val job = launch {
                    val response: HttpResponse = universeClient.ktorClient.get("http://127.0.0.1:29979/status/hello") {
                        timeout {
                            requestTimeoutMillis = 1000
                        }
                    }
                    println("$i " + response.readText())
                }
            }
            delay(10000)
            universeServer.stop()
        }
    }
}