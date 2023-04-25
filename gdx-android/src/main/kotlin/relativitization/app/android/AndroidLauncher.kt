package relativitization.app.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.coroutineScope
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.Level
import relativitization.client.UniverseClient
import relativitization.game.GdxSettings
import relativitization.game.RelativitizationGame
import relativitization.server.UniverseServer
import relativitization.universe.game.UniverseClientSettings
import relativitization.universe.game.UniverseServerSettings
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.game.GameUniverseInitializer
import relativitization.utils.ServerPort
import kotlin.random.Random

@ExperimentalCoroutinesApi
class AndroidLauncher : AppCompatActivity(), AndroidFragmentApplication.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        // This doesn't work
        //val isLoggerRelease: Boolean =
        // (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

        // Reduce logging for release build
        val isLoggerRelease: Boolean = BuildConfig.VERSION_NAME.matches(Regex(".*Debug"))

        // Set log level for default logger - the one used in android app
        if (isLoggerRelease) {
            RelativitizationLogManager.setSimpleLoggerLevel(Level.OFF)
        } else {
            RelativitizationLogManager.setSimpleLoggerLevel(Level.DEBUG)
        }

        super.onCreate(savedInstanceState)

        GameUniverseInitializer.initialize()

        val adminPasswordRandom = Random(System.currentTimeMillis())
        val adminPassword: String = List(10) {
            adminPasswordRandom.nextInt(0, 10)
        }.joinToString(separator = "")

        val serverAddress = "127.0.0.1"
        val serverPort: Int = ServerPort.findAvailablePort()

        val universeServerSettings = UniverseServerSettings(
            programDir = applicationContext.filesDir.toString(),
            adminPassword = adminPassword
        )
        val universeClientSettings = UniverseClientSettings(
            programDir = applicationContext.filesDir.toString(),
            adminPassword = adminPassword,
            serverAddress = serverAddress,
            serverPort = serverPort,
        )

        val universeServer = UniverseServer(
            universeServerSettings = universeServerSettings,
            serverAddress = serverAddress,
            serverPort = serverPort
        )
        val universeClient = UniverseClient(universeClientSettings)

        // Default gdx settings
        val defaultGdxSettings = GdxSettings()
        defaultGdxSettings.smallFontSize = 24
        defaultGdxSettings.normalFontSize = 36
        defaultGdxSettings.bigFontSize = 48
        defaultGdxSettings.hugeFontSize = 60
        defaultGdxSettings.imageScale = 1.5f
        defaultGdxSettings.worldMapInfoPaneSplitAmount = 0.55f
        defaultGdxSettings.infoPaneSplitAmount = 0.7f
        defaultGdxSettings.isControlBarTop = false

        val relativitizationGameFragment = RelativitizationGameFragment(
            universeClient = universeClient,
            defaultGdxSettings = defaultGdxSettings,
        ) {
            runBlocking {
                universeServer.stop()
                universeClient.stop()
            }
            finishAndRemoveTask()
        }

        val trans: FragmentTransaction = supportFragmentManager.beginTransaction()

        trans.replace(android.R.id.content, relativitizationGameFragment)

        trans.commit()

        lifecycle.coroutineScope.launch(Dispatchers.IO) {
            universeServer.start()
        }

        lifecycle.coroutineScope.launch(Dispatchers.IO.limitedParallelism(1)) {
            universeClient.start()
        }
    }

    override fun exit() {}
}

class RelativitizationGameFragment(
    private val universeClient: UniverseClient,
    private val defaultGdxSettings: GdxSettings,
    private val exit: () -> Unit,
) : AndroidFragmentApplication() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = initializeForView(
            RelativitizationGame(
                universeClient = universeClient,
                defaultGdxSettings = defaultGdxSettings,
                exit = exit,
            )
        )

        view.keepScreenOn = true

        return view
    }
}
