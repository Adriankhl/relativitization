package relativitization.communication

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import relativitization.client.UniverseClient
import relativitization.server.UniverseServer
import relativitization.universe.game.GameUniverseInitializer
import relativitization.universe.game.UniverseClientSettings
import relativitization.universe.game.UniverseServerSettings
import kotlin.test.Test

internal class ServerStatusTest {
    @Test
    fun newServerStatusTest() {
        GameUniverseInitializer.initialize()

        val universeServer = UniverseServer(UniverseServerSettings(adminPassword = "pwd"))
        val universeClient = UniverseClient(UniverseClientSettings(adminPassword = "pwd"))
        runBlocking {
            launch {
                universeServer.start()
            }
            delay(1000)
            val status = universeClient.httpGetUniverseServerStatus()
            //println(status)
            assert(!status.isServerWaitingInput)
            universeServer.stop()
        }
    }

    @Test
    fun newUniverseStatusTest() {
        GameUniverseInitializer.initialize()

        val universeServer = UniverseServer(UniverseServerSettings(adminPassword = "pwd"))
        val universeClient = UniverseClient(UniverseClientSettings(adminPassword = "pwd"))
        runBlocking {
            launch {
                universeServer.start()
            }

            delay(1000)

            while (universeClient.httpPostNewUniverse() != HttpStatusCode.OK) {
                delay(1000)
            }
            val status = universeClient.httpGetUniverseServerStatus()
            //println(status)
            assert(status.hasUniverse)
            universeServer.stop()
        }
    }
}