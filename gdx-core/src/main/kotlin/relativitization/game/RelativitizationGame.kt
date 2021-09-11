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
import relativitization.universe.utils.RelativitizationLogManager

class RelativitizationGame(val universeClient: UniverseClient, private val universeServer: UniverseServer) : Game() {

    val gdxSettings: GdxSettings = GdxSettings.loadOrDefault(universeClient.universeClientSettings.programDir)
    val onGdxSettingsChangeFunctionList: MutableList<() -> Unit> = mutableListOf()

    // call when gdx setting is changed
    val changeGdxSettings: () -> Unit = {
        onGdxSettingsChangeFunctionList.forEach { it() }
    }

    val assets: Assets = Assets(gdxSettings)

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

            if (Gdx.graphics.width != gdxSettings.windowsWidth ||
                Gdx.graphics.height != gdxSettings.windowsHeight) {
                logger.debug("Cannot adjust the width and height to the value in settings," +
                        "store the current value")

                // Set graphics height
                gdxSettings.windowsWidth = Gdx.app.graphics.width
                gdxSettings.windowsHeight = Gdx.app.graphics.height

                // Set the windows size again in case if the size is enforced by the os
                // e.g. Sway wm
                Gdx.graphics.setWindowedMode(gdxSettings.windowsWidth, gdxSettings.windowsHeight)
            }
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

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}