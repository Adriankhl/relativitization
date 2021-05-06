package relativitization.communication

import kotlinx.coroutines.*
import kotlin.test.Test
import relativitization.client.UniverseClient
import relativitization.server.UniverseServer

internal class ServerStatusTest {
    @Test
    fun newServerStatus() {
        val universeServer = UniverseServer("pwd")
        val universeClient = UniverseClient("pwd")
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
        val universeServer = UniverseServer("pwd")
        val universeClient = UniverseClient("pwd")
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