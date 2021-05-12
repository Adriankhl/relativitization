package relativitization.communication

import kotlinx.coroutines.*
import kotlin.test.Test
import relativitization.client.UniverseClient
import relativitization.server.UniverseServer
import relativitization.universe.UniverseClientSettings
import relativitization.universe.UniverseServerSettings

internal class ServerStatusTest {
    @Test
    fun newServerStatus() {
        val universeServer = UniverseServer(UniverseServerSettings("pwd"))
        val universeClient = UniverseClient(UniverseClientSettings("pwd"))
        runBlocking {
            launch {
                universeServer.start()
            }
            delay(1000)
            val status = universeClient.httpGetUniverseServerStatus()
            println(status)
            assert(status.waitingInput == false)
            universeServer.stop()
        }
    }

    @Test
    fun newUniverseStatus() {
        val universeServer = UniverseServer(UniverseServerSettings("pwd"))
        val universeClient = UniverseClient(UniverseClientSettings("pwd"))
        runBlocking {
            launch {
                universeServer.start()
            }
            delay(1000)
            universeClient.httpPostNewUniverse()
            val status = universeClient.httpGetUniverseServerStatus()
            println(status)
            assert(status.hasUniverse == true)
            universeServer.stop()
        }
    }
}