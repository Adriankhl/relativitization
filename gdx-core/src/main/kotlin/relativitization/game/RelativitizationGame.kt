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

    val gdxSetting: GdxSetting = GdxSetting()
    val onGdxSettingsChangeFunctionList: MutableList<() -> Unit> = mutableListOf()

    // call when gdx setting is changed
    val changeGdxSetting: () -> Unit = {
        onGdxSettingsChangeFunctionList.forEach { it() }
    }

    val assets: Assets = Assets()

    private lateinit var backgroundMusic: Music

    var isGameStarted: Boolean = false

    override fun create() {
        Gdx.input.setCatchKey(Input.Keys.BACK, true)
        Gdx.graphics.isContinuousRendering = gdxSetting.continuousRendering

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
            gdxSetting.windowsWidth > 39 &&
            gdxSetting.windowsHeight > 39
        ) {
            Gdx.graphics.setWindowedMode(gdxSetting.windowsWidth, gdxSetting.windowsHeight)
        }
    }

    private fun startMusic() {
        if (gdxSetting.musicVolume < 0.01) return

        backgroundMusic = assets.getBackgroundMusic()

        backgroundMusic.isLooping = true
        backgroundMusic.volume = 0.4f * gdxSetting.musicVolume
        backgroundMusic.play()
    }

    fun restartMusic() {
        backgroundMusic.stop()
        backgroundMusic.volume = 0.4f * gdxSetting.musicVolume
        backgroundMusic.play()
    }

    fun clearOnChangeFunctionList() {
        onGdxSettingsChangeFunctionList.clear()
        universeClient.clearOnChangeFunctionList()
    }

    fun clear() {
        clearOnChangeFunctionList()
        universeClient.clear()
    }
}