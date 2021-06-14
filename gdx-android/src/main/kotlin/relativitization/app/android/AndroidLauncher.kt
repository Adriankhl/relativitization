package relativitization.app.android

import android.os.Bundle
import androidx.lifecycle.*
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import relativitization.client.UniverseClient
import relativitization.game.RelativitizationGame
import relativitization.server.UniverseServer
import relativitization.universe.UniverseClientSettings
import relativitization.universe.UniverseServerSettings
import relativitization.universe.utils.AndroidLogger
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.random.Random

class AndroidLauncher : AndroidApplication() {

    private lateinit var relativitizationGame: RelativitizationGame

    override fun onCreate(savedInstanceState: Bundle?) {
        // Determine print logger or not
        RelativitizationLogManager.isAndroid = true
        AndroidLogger.showLog = true

        super.onCreate(savedInstanceState)

        val config = AndroidApplicationConfiguration()
        config.useImmersiveMode = true


        val adminPassword: String = List(10) { Random.nextInt(0, 10) }.joinToString(separator = "")

        val universeServerSettings = UniverseServerSettings(
            programDir = context.filesDir.toString(),
            adminPassword = adminPassword
        )
        val universeClientSettings = UniverseClientSettings(
            programDir = context.filesDir.toString(),
            adminPassword = adminPassword
        )

        val universeServer: UniverseServer = UniverseServer(universeServerSettings)
        val universeClient: UniverseClient = UniverseClient(universeClientSettings)


        relativitizationGame = RelativitizationGame(universeClient, universeServer)

        initialize(relativitizationGame, config)
    }

    override fun onStart() {
        super.onStart()

        /*
        runBlocking {
            launch(Dispatchers.IO) {
                relativitizationGame.universeServer.start()
            }

            launch(Dispatchers.IO) {
                relativitizationGame.universeClient.start()
            }
        }

         */
    }
}
