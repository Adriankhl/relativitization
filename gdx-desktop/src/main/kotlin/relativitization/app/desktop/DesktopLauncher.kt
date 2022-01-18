package relativitization.app.desktop

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.HdpiMode
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import relativitization.client.UniverseClient
import relativitization.game.RelativitizationGame
import relativitization.game.utils.Assets
import relativitization.server.UniverseServer
import relativitization.universe.UniverseClientSettings
import relativitization.universe.UniverseServerSettings
import relativitization.universe.utils.RelativitizationLogManager
import relativitization.utils.ServerPort
import java.io.File
import relativitization.universe.maths.random.Rand

private val logger = RelativitizationLogManager.getLogger()

@ExperimentalCoroutinesApi
fun main() {

    // Set log level
    Configurator.setRootLevel(Level.DEBUG)
    Configurator.setLevel("Translation", Level.ERROR)
    Configurator.setLevel("UniverseServerInternal", Level.ERROR)
    Configurator.setLevel("UniverseClient", Level.ERROR)
    Configurator.setLevel("ActorFunction", Level.ERROR)

    // pack images to atlas
    packImages()

    val config = Lwjgl3ApplicationConfiguration()
    config.setTitle("Relativitization")
    config.setWindowIcon(Files.FileType.Internal, "./${Assets.dir()}/images/normal/logo/logo.png")
    config.setHdpiMode(HdpiMode.Logical)

    val adminPassword: String = List(10) { Rand.rand().nextInt(0, 10) }.joinToString(separator = "")

    val serverAddress: String = "127.0.0.1"
    val serverPort: Int = ServerPort.findAvailablePort()

    val universeServerSettings = UniverseServerSettings(adminPassword = adminPassword)
    val universeClientSettings = UniverseClientSettings(
        adminPassword = adminPassword,
        serverAddress = serverAddress,
        serverPort = serverPort,
    )

    runBlocking {
        val universeServer: UniverseServer = UniverseServer(
            universeServerSettings = universeServerSettings,
            serverAddress = serverAddress,
            serverPort = serverPort
        )
        val universeClient: UniverseClient = UniverseClient(universeClientSettings)

        launch(Dispatchers.Default.limitedParallelism(1)) {
            val game = RelativitizationGame(universeClient) {
                runBlocking { universeServer.stop() }
            }

            try {
                Lwjgl3Application(game, config)
            } finally {
                //game.dispose()
            }
        }

        launch(Dispatchers.IO) {
            universeServer.start()
        }
        launch(Dispatchers.IO.limitedParallelism(1)) {
            universeClient.start()
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
    val input: String = "./${Assets.dir()}/images/pack"
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
