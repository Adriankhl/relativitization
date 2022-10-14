package relativitization.communication

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import relativitization.client.UniverseClient
import relativitization.server.UniverseServer
import relativitization.universe.UniverseClientSettings
import relativitization.universe.UniverseServerSettings
import kotlin.test.Test

internal class ServerStatusTest {
    @Test
    fun newServerStatus() {
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
    fun newUniverseStatus() {
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