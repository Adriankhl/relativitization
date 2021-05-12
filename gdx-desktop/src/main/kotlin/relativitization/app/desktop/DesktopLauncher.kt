package relativitization.app.desktop

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.HdpiMode
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import relativitization.client.UniverseClient
import relativitization.game.RelativitizationGame
import relativitization.server.UniverseServer
import relativitization.universe.UniverseClientSettings
import relativitization.universe.UniverseServerSettings
import java.io.File
import kotlin.random.Random

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {

        // pack images to atlas
        packImages()

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

    private fun packImages() {
        val settings = TexturePacker.Settings()

        settings.maxWidth = 4096
        settings.maxHeight = 4096
        settings.combineSubdirectories = true
        settings.pot = true
        settings.fast = true

        // Prevent pixelated
        settings.filterMag = Texture.TextureFilter.MipMapLinearLinear
        settings.filterMin = Texture.TextureFilter.MipMapLinearLinear

        // Pack if outdated
        val atlasFileName: String = "relativitization-asset"
        val atlasFile: File = File("$atlasFileName.atlas")
        val input: String = "./images/pack"
        if (!atlasFile.exists() || isAtlasOutdated(atlasFile, input)) {
            logger.info("Pack atlas")
            TexturePacker.process(settings, input, ".", atlasFileName)
        }
    }

    /**
     * Check if the atlas file needs to be updated
     */
    private fun isAtlasOutdated(atlasFile: File, path: String): Boolean {
        val atlasFileLastModified: Long = atlasFile.lastModified()

        val allFiles: Sequence<File> = File(path).walkTopDown()

        return allFiles.map {
            if (it.extension in listOf("png", "jpg", "jpeg")) {
                it.lastModified() > atlasFileLastModified
            } else {
                false
            }
        }.contains(true)
    }

    private val logger = LogManager.getLogger()
}