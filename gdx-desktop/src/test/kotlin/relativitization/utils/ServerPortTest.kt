package relativitization.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import relativitization.server.UniverseServer
import relativitization.universe.game.UniverseServerSettings
import kotlin.test.Test

internal class ServerPortTest {
    @Test
    fun availablePortTest() {
        assert(ServerPort.findAvailablePort() == 29979)

        val universeServer = UniverseServer(UniverseServerSettings(""))

        assert(ServerPort.findAvailablePort() == 29979)

        runBlocking {
            launch {
                universeServer.start()
            }

            delay(1000)

            assert(ServerPort.findAvailablePort() == 29980)

            universeServer.stop()
        }
    }
}