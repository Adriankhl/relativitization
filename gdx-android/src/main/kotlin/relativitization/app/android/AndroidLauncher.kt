package relativitization.app.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import relativitization.client.UniverseClient
import relativitization.game.RelativitizationGame
import relativitization.server.UniverseServer
import relativitization.universe.UniverseClientSettings
import relativitization.universe.UniverseServerSettings
import relativitization.universe.utils.AndroidLogger
import relativitization.universe.utils.RelativitizationLogManager
import java.util.concurrent.Executors
import kotlin.random.Random

class AndroidLauncher : AndroidApplication() {

    init {
        // Determine print logger or not
        RelativitizationLogManager.isAndroid = true
        AndroidLogger.showLog = true
    }

    private val adminPassword: String = List(10) { Random.nextInt(0, 10) }.joinToString(separator = "")

    private val universeServerSettings = UniverseServerSettings(
        programDir = context.filesDir.toString(),
        adminPassword = adminPassword
    )
    private val universeClientSettings = UniverseClientSettings(
        programDir = context.filesDir.toString(),
        adminPassword = adminPassword
    )

    private val universeServer: UniverseServer = UniverseServer(universeServerSettings)
    private val universeClient: UniverseClient = UniverseClient(universeClientSettings)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = AndroidApplicationConfiguration()
        config.useImmersiveMode = true

        val game = RelativitizationGame(universeClient, universeServer)
        initialize(game, config)
/*
            launch(Dispatchers.IO) {
                universeServer.start()
            }

            launch {
                universeClient.start()
            }
 */
    }
}
