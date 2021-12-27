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
import relativitization.client.UniverseClient
import relativitization.game.RelativitizationGame
import relativitization.server.UniverseServer
import relativitization.universe.UniverseClientSettings
import relativitization.universe.UniverseServerSettings
import relativitization.universe.utils.AndroidLogger
import relativitization.universe.utils.RelativitizationLogManager
import relativitization.utils.ServerPort
import relativitization.universe.maths.random.Rand

@ExperimentalCoroutinesApi
class AndroidLauncher : AppCompatActivity(), AndroidFragmentApplication.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        RelativitizationLogManager.isAndroid = true
        AndroidLogger.showLog = true

        super.onCreate(savedInstanceState)

        val adminPassword: String = List(10) { Rand.rand().nextInt(0, 10) }.joinToString(separator = "")

        val serverAddress: String = "127.0.0.1"
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


        val universeServer: UniverseServer = UniverseServer(
            universeServerSettings = universeServerSettings,
            serverAddress = serverAddress,
            serverPort = serverPort
        )
        val universeClient: UniverseClient = UniverseClient(universeClientSettings)

        val relativitizationGameFragment =
            RelativitizationGameFragment(universeClient, universeServer)

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

    override fun exit() { }
}

class RelativitizationGameFragment(
    private val universeClient: UniverseClient,
    private val universeServer: UniverseServer,
) : AndroidFragmentApplication() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return initializeForView(RelativitizationGame(universeClient, universeServer))
    }
}
