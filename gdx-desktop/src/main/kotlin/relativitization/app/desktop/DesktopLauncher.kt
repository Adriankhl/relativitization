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
import kotlin.random.Random

internal object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = Lwjgl3ApplicationConfiguration()
        config.setTitle("Relativitization")
        config.setWindowIcon(Files.FileType.External, "images/normal/logo/logo.png")
        config.setHdpiMode(HdpiMode.Logical)

        val adminPassword: String= List(10) { Random.nextInt(0, 10) }.joinToString(separator="")

        runBlocking {
            val universeServer: UniverseServer = UniverseServer(adminPassword)
            val universeClient: UniverseClient = UniverseClient(adminPassword)
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