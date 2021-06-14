package relativitization.app.android

import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import relativitization.client.UniverseClient
import relativitization.game.RelativitizationGame
import relativitization.server.UniverseServer
import relativitization.universe.UniverseClientSettings
import relativitization.universe.UniverseServerSettings
import relativitization.universe.utils.AndroidLogger
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.random.Random

class AndroidLauncher : FragmentActivity(), AndroidFragmentApplication.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        RelativitizationLogManager.isAndroid = true
        AndroidLogger.showLog = true

        super.onCreate(savedInstanceState)


        val adminPassword: String = List(10) { Random.nextInt(0, 10) }.joinToString(separator = "")

        val universeServerSettings = UniverseServerSettings(
            programDir = applicationContext.filesDir.toString(),
            adminPassword = adminPassword
        )
        val universeClientSettings = UniverseClientSettings(
            programDir = applicationContext.filesDir.toString(),
            adminPassword = adminPassword
        )

        val universeServer: UniverseServer = UniverseServer(universeServerSettings)
        val universeClient: UniverseClient = UniverseClient(universeClientSettings)


        val relativitizationGameFragment = RelativitizationGameFragment(universeClient, universeServer)

        val trans: FragmentTransaction = supportFragmentManager.beginTransaction()

        trans.replace(android.R.id.content, relativitizationGameFragment)

        trans.commit()
    }

    override fun exit() {
    }
}

class RelativitizationGameFragment(
    private val universeClient: UniverseClient,
    private val universeServer: UniverseServer,
) : AndroidFragmentApplication() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return initializeForView(RelativitizationGame(universeClient, universeServer))
    }
}
