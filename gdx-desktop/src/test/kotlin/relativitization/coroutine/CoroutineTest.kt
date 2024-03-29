package relativitization.coroutine

import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import relativitization.client.UniverseClient
import relativitization.server.UniverseServer
import relativitization.universe.game.UniverseClientSettings
import relativitization.universe.game.UniverseServerSettings
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
            //println("Launched universe server")

            delay(1000)

            val helloList: List<String> = (1..100).map {
                //println("New Job")
                async {
                    val response: HttpResponse =
                        universeClient.ktorClient.get("http://127.0.0.1:29979/status/hello") {
                            timeout {
                                requestTimeoutMillis = 1000
                            }
                        }

                    response.bodyAsText()
                }
            }.awaitAll()

            delay(10000)

            universeServer.stop()

            helloList.forEach {
                assert(it == "Hello, world!")
            }
        }
    }

    @Test
    fun wrongPasswordTest() {
        Configurator.setRootLevel(Level.ERROR)

        val universeServer = UniverseServer(UniverseServerSettings(adminPassword = "wrong"))
        val universeClient = UniverseClient(UniverseClientSettings(adminPassword = "pwd"))

        runBlocking {
            launch(Dispatchers.IO) {
                universeServer.start()
            }
            //println("Launched universe server")

            delay(1000)

            //println("create universe")

            universeClient.httpPostNewUniverse()

            //println("Done create universe")

            universeServer.stop()
        }
    }

    @Test
    fun createUniverseTest() {
        Configurator.setRootLevel(Level.ERROR)

        val universeServer = UniverseServer(UniverseServerSettings(adminPassword = "pwd"))
        val universeClient = UniverseClient(UniverseClientSettings(adminPassword = "pwd"))

        runBlocking {
            launch(Dispatchers.IO) {
                universeServer.start()
            }

            //println("Launched universe server")

            delay(1000)

            //println("create universe")

            universeClient.httpPostNewUniverse()

            //println("Done create universe")

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

            //println("Launched universe server")

            delay(1000)

            //println("create universe")

            universeClient.httpPostNewUniverse()

            //println("posted normal")

            val status: HttpStatusCode = try {
                val response: HttpResponse =
                    universeClient.ktorClient.post("http://127.0.0.1:29979/create/new") {
                        contentType(ContentType.Application.Json)
                        setBody("wrong body")
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


            //println("Done create universe")

            //println(status)

            universeServer.stop()

            assert(!status.isSuccess())
        }
    }

    @Test
    fun connectionFailTest() {
        val universeServer = UniverseServer(UniverseServerSettings(adminPassword = "pwd"))
        val universeClient = UniverseClient(UniverseClientSettings(adminPassword = "pwd"))

        runBlocking {

            //println("before")

            try {
                val response: HttpResponse = universeClient.ktorClient.get("http://127.0.0.1:123")
                assert(!response.status.isSuccess())
                //println(response)
            } catch (cause: Throwable) {
                //println("Error: $cause")
            }

            //println("After")

            universeServer.stop()
        }
    }
}