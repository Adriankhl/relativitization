package relativitization.app.desktop

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.glutils.HdpiMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import relativitization.client.UniverseClient
import relativitization.game.RelativitizationGame
import relativitization.server.UniverseServer
import relativitization.universe.UniverseClientSettings
import relativitization.universe.UniverseServerSettings
import kotlin.random.Random

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = Lwjgl3ApplicationConfiguration()
        config.setTitle("Relativitization")
        config.setWindowIcon(Files.FileType.Internal, "images/normal/logo/logo.png")
        config.setHdpiMode(HdpiMode.Logical)

        val adminPassword: String = List(10) { Random.nextInt(0, 10) }.joinToString(separator = "")

        val universeServerSettings = UniverseServerSettings(adminPassword = adminPassword)
        val universeClientSettings = UniverseClientSettings(adminPassword = adminPassword)

        runBlocking {
            val universeServer: UniverseServer = UniverseServer(universeServerSettings)
            val universeClient: UniverseClient = UniverseClient(universeClientSettings)
            launch(Dispatchers.IO) {
                universeServer.start()
            }
            launch {
                universeClient.start()
            }

            launch {
                val game = RelativitizationGame(universeClient, universeServer)
                Lwjgl3Application(game, config)
            }
        }
    }
}