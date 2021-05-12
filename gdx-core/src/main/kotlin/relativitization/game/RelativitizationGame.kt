package relativitization.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import relativitization.client.UniverseClient
import relativitization.server.UniverseServer

class RelativitizationGame(val universeClient: UniverseClient, val universeServer: UniverseServer) : Game() {

    var setting: GameSetting = GameSetting()

    var backGroundMusicLocation: String = "music/Alexander Ehlers - Warped.mp3"

    lateinit var backgroundMusic: Music

    override fun create() {
        Gdx.graphics.isContinuousRendering = setting.continuousRendering
        startMusic()
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
}