package relativitization.game

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import kotlinx.coroutines.runBlocking
import relativitization.client.UniverseClient
import relativitization.game.utils.Assets
import relativitization.server.UniverseServer

class RelativitizationGame(val universeClient: UniverseClient, val universeServer: UniverseServer) : Game() {

    private var setting: GameSetting = GameSetting()

    private val assets: Assets = Assets()

    private lateinit var backgroundMusic: Music

    override fun create() {
        Gdx.input.setCatchKey(Input.Keys.BACK, true)
        Gdx.graphics.isContinuousRendering = setting.continuousRendering

        assets.loadAll()

        restoreSize()
        startMusic()

        setScreen(MainMenuScreen(assets))
    }


    private fun restoreSize() {
        if (Gdx.app.type == Application.ApplicationType.Desktop &&
            setting.windowsWidth > 39 &&
            setting.windowsHeight > 39
        ) {
            Gdx.graphics.setWindowedMode(setting.windowsWidth, setting.windowsHeight)
        }
    }

    private fun startMusic() {
        if (setting.musicVolume < 0.01) return

        backgroundMusic = assets.getBackgroundMusic()

        backgroundMusic.isLooping = true
        backgroundMusic.volume = 0.4f * setting.musicVolume
        backgroundMusic.play()
    }

    override fun dispose() {
        runBlocking {
            backgroundMusic.stop()
            universeClient.stop()
            universeServer.stop()
        }
    }
}