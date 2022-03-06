package relativitization.app.android

import android.app.Application
import android.content.pm.ApplicationInfo
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
import org.apache.logging.log4j.core.config.Configurator
import relativitization.client.UniverseClient
import relativitization.game.RelativitizationGame
import relativitization.server.UniverseServer
import relativitization.universe.UniverseClientSettings
import relativitization.universe.UniverseServerSettings
import relativitization.universe.maths.random.Rand
import relativitization.utils.ServerPort

@ExperimentalCoroutinesApi
class AndroidLauncher : AppCompatActivity(), AndroidFragmentApplication.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Reduce logging for release build
        val isLoggerRelease: Boolean =
            (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

        // Set log level
        if (isLoggerRelease) {
            Configurator.setRootLevel(Level.OFF)
        } else {
            Configurator.setRootLevel(Level.DEBUG)
            Configurator.setLevel("Translation", Level.ERROR)
            Configurator.setLevel("UniverseServerInternal", Level.ERROR)
            Configurator.setLevel("UniverseClient", Level.ERROR)
            Configurator.setLevel("ActorFunction", Level.ERROR)
        }

        super.onCreate(savedInstanceState)

        val adminPassword: String = List(10) {
            Rand.rand().nextInt(0, 10) }.joinToString(separator = ""
        )

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

        val relativitizationGameFragment = RelativitizationGameFragment(
            universeClient = universeClient,
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
    private val exit: () -> Unit,
) : AndroidFragmentApplication() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return initializeForView(
            RelativitizationGame(
                universeClient = universeClient,
                defaultScale = 1.5,
                exit = exit,
            )
        )
    }
}
