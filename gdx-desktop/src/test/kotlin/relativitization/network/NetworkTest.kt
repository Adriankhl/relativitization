package relativitization.network

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import relativitization.client.UniverseClient
import relativitization.server.UniverseServer
import relativitization.universe.utils.CoroutineBoolean

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
            var textResponse: String = ""
            val coroutineBoolean: CoroutineBoolean = CoroutineBoolean(true)
            /*
            while (coroutineBoolean.isTrue()) {
                delay(1)
                println("New job")
                val job = launch {
                    println("Start job")
                    val response: HttpResponse = universeClient.client.get("http://127.0.0.1:29979/status/hello")
                    // println("Complete request")
                    // textResponse = response.readText()
                    // coroutineBoolean.set(false)
                }
                delay(1000)
                println("Cancel job")
                job.cancel()
            }
            */
            while (coroutineBoolean.isTrue()) {
                println("New Job")
                val job = launch {
                    val response: HttpResponse = universeClient.client.get("http://127.0.0.1:29979/status/hello") {
                        timeout {
                            requestTimeoutMillis = 1000
                        }
                    }
                    println("Complete request")
                    textResponse = response.readText()
                    coroutineBoolean.set(false)
                }
            }
            println(textResponse)
            universeServer.stop()
        }
    }
}