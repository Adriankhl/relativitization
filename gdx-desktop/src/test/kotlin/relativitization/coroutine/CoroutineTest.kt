package relativitization.coroutine

import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import relativitization.client.UniverseClient
import relativitization.server.UniverseServer
import relativitization.universe.UniverseClientSettings
import relativitization.universe.UniverseServerSettings
import kotlin.test.Test

internal class CoroutineTest {
    @Test
    fun helloTest() {
        val universeServer = UniverseServer(UniverseServerSettings(adminPassword = "pwd"))
        val universeClient = UniverseClient(UniverseClientSettings(adminPassword = "pwd"))
        runBlocking {
            launch(Dispatchers.IO) {
                universeServer.start()
            }
            println("Launched universe server")

            delay(1000)

            for (i in 1..100) {
                println("New Job")
                launch {
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

    @Test
    fun wrongPasswordTest() {
        Configurator.setRootLevel(Level.DEBUG)

        val universeServer = UniverseServer(UniverseServerSettings(adminPassword = "wrong"))
        val universeClient = UniverseClient(UniverseClientSettings(adminPassword = "pwd"))

        runBlocking {
            launch(Dispatchers.IO) {
                universeServer.start()
            }
            println("Launched universe server")

            delay(1000)

            println("create universe")
            universeClient.httpPostNewUniverse()
            println("Done create universe")

            universeServer.stop()
        }
    }

    @Test
    fun createUniverseTest() {
        Configurator.setRootLevel(Level.DEBUG)

        val universeServer = UniverseServer(UniverseServerSettings(adminPassword = "pwd"))
        val universeClient = UniverseClient(UniverseClientSettings(adminPassword = "pwd"))

        runBlocking {
            launch(Dispatchers.IO) {
                universeServer.start()
            }
            println("Launched universe server")

            delay(1000)

            println("create universe")
            universeClient.httpPostNewUniverse()
            println("Done create universe")

            universeServer.stop()
        }
    }


    @Test
    fun createUniverseFailTest() {
        val universeServer = UniverseServer(UniverseServerSettings(adminPassword = "pwd"))
        val universeClient = UniverseClient(UniverseClientSettings(adminPassword = "pwd"))

        runBlocking {
            launch(Dispatchers.IO) {
                universeServer.start()
            }
            println("Launched universe server")

            delay(1000)

            println("create universe")
            universeClient.httpPostNewUniverse()
            println("posted normal")
            val status: HttpStatusCode = try {
                val response: HttpResponse = universeClient.ktorClient.post("http://127.0.0.1:29979/create/new") {
                    contentType(ContentType.Application.Json)
                    body = "sdfsdf"
                    timeout {
                        requestTimeoutMillis = 1000
                        connectTimeoutMillis = 1000
                        socketTimeoutMillis = 1000
                    }
                }
                response.status
            } catch (cause: ResponseException) {
                cause.response.status
            } catch (cause: Throwable) {
                HttpStatusCode.NotFound
            }

            println("Done create universe")
            println(status)
            universeServer.stop()
        }
    }

    @Test
    fun connectionFailTest() {
        val universeServer = UniverseServer(UniverseServerSettings(adminPassword = "pwd"))
        val universeClient = UniverseClient(UniverseClientSettings(adminPassword = "pwd"))

        runBlocking {
            println("before")
            try {
                val response: HttpResponse = universeClient.ktorClient.get("http://127.0.0.1:123")
                println(response)
            } catch(cause: Throwable) {
                println("Error: $cause")
            }
            println("After")
            universeServer.stop()
        }
    }
}