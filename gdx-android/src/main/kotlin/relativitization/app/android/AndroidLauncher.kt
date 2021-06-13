package relativitization.app.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Level.OFF
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import relativitization.client.UniverseClient
import relativitization.game.RelativitizationGame
import relativitization.server.UniverseServer
import relativitization.universe.UniverseClientSettings
import relativitization.universe.UniverseServerSettings
import java.util.concurrent.Executors
import kotlin.random.Random

class AndroidLauncher : AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = AndroidApplicationConfiguration()
        config.useImmersiveMode = true


        val adminPassword: String = List(10) { Random.nextInt(0, 10) }.joinToString(separator = "")

        val universeServerSettings = UniverseServerSettings(adminPassword = adminPassword)
        val universeClientSettings = UniverseClientSettings(adminPassword = adminPassword)

        val gdxExecutorService = Executors.newSingleThreadExecutor()

        runBlocking {
            val universeServer: UniverseServer = UniverseServer(universeServerSettings)
            val universeClient: UniverseClient = UniverseClient(universeClientSettings)

            launch(gdxExecutorService.asCoroutineDispatcher()) {
                val game = RelativitizationGame(universeClient, universeServer)
                try {
                    initialize(game, config)
                } finally {
                    game.dispose()
                }
            }

            launch(Dispatchers.IO) {
                universeServer.start()
            }
            launch {
                universeClient.start()
            }
        }

        gdxExecutorService.shutdown()
    }

    companion object {
        init {
            Configurator.setRootLevel(OFF)
        }
        private val logger = LogManager.getLogger()
    }
}
