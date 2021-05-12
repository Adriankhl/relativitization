package relativitization.game

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import kotlinx.coroutines.runBlocking
import relativitization.client.UniverseClient
import relativitization.server.UniverseServer

class RelativitizationGame(val universeClient: UniverseClient, val universeServer: UniverseServer) : Game() {

    var setting: GameSetting = GameSetting()

    var backGroundMusicLocation: String = "music/Alexander Ehlers - Warped.mp3"

    lateinit var backgroundMusic: Music

    override fun create() {
        Gdx.input.setCatchKey(Input.Keys.BACK, true)
        Gdx.graphics.isContinuousRendering = setting.continuousRendering

        restoreSize()
        startMusic()

        setScreen(MainMenuScreen())
    }


    fun restoreSize() {
        if (Gdx.app.type == Application.ApplicationType.Desktop &&
            setting.windowsWidth > 39 &&
            setting.windowsHeight > 39
        ) {
            Gdx.graphics.setWindowedMode(setting.windowsWidth, setting.windowsHeight)
        }
    }

    fun startMusic() {
        if (setting.musicVolume < 0.01) return

        val backGroundMusicFile = Gdx.files.internal(backGroundMusicLocation)
        if (backGroundMusicFile.exists()) {
            backgroundMusic = Gdx.audio.newMusic(backGroundMusicFile)
            backgroundMusic.isLooping = true
            backgroundMusic.volume = 0.4f * setting.musicVolume
            backgroundMusic.play()
        }
    }

    override fun dispose() {
        runBlocking {
            universeClient.stop()
            universeServer.stop()
        }
    }
}