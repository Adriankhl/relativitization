package relativitization.communication

import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.junit.jupiter.api.Test
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
            val status = universeClient.getUniverseServerStatus()
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
            universeClient.postNewUniverse()
            val status = universeClient.getUniverseServerStatus()
            println(status)
            assert(status.hasUniverse == true)
            universeServer.stop()
        }
    }
}