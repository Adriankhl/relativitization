package relativitization.game

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import kotlinx.coroutines.runBlocking
import relativitization.client.UniverseClient
import relativitization.game.screens.MainMenuScreen
import relativitization.game.utils.Assets
import relativitization.server.UniverseServer

class RelativitizationGame(val universeClient: UniverseClient, private val universeServer: UniverseServer) : Game() {

    val gdxSettings: GdxSettings = GdxSettings()
    val onGdxSettingsChangeFunctionList: MutableList<() -> Unit> = mutableListOf()

    // call when gdx setting is changed
    val changeGdxSettings: () -> Unit = {
        onGdxSettingsChangeFunctionList.forEach { it() }
    }

    val assets: Assets = Assets()

    private lateinit var backgroundMusic: Music

    var isGameStarted: Boolean = false

    override fun create() {
        Gdx.input.setCatchKey(Input.Keys.BACK, true)
        Gdx.graphics.isContinuousRendering = gdxSettings.continuousRendering

        assets.loadAll()

        restoreSize()
        startMusic()

        setScreen(MainMenuScreen(this))
    }


    override fun dispose() {
        clear()
        runBlocking {
            backgroundMusic.stop()
            assets.dispose()
            universeClient.stop()
            universeServer.stop()
        }
    }


    fun restoreSize() {
        if (Gdx.app.type == Application.ApplicationType.Desktop &&
            gdxSettings.windowsWidth > 39 &&
            gdxSettings.windowsHeight > 39
        ) {
            Gdx.graphics.setWindowedMode(gdxSettings.windowsWidth, gdxSettings.windowsHeight)
        }
    }

    private fun startMusic() {
        if (gdxSettings.musicVolume < 0.01) return

        backgroundMusic = assets.getBackgroundMusic()

        backgroundMusic.isLooping = true
        backgroundMusic.volume = 0.4f * gdxSettings.musicVolume
        backgroundMusic.play()
    }

    fun restartMusic() {
        backgroundMusic.stop()
        backgroundMusic.volume = 0.4f * gdxSettings.musicVolume
        backgroundMusic.play()
    }

    fun clearOnChangeFunctionList() {
        onGdxSettingsChangeFunctionList.clear()
        runBlocking {
            universeClient.clearOnChangeFunctionList()
        }
    }

    fun clear() {
        clearOnChangeFunctionList()
        runBlocking {
            universeClient.clear()
        }
    }
}