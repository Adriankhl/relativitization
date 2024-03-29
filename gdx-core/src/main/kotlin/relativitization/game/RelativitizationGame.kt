package relativitization.game

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import relativitization.client.UniverseClient
import relativitization.game.screens.MainMenuScreen
import relativitization.game.utils.Assets
import relativitization.universe.core.utils.RelativitizationLogManager
import java.io.File

/**
 * The main game graphical interface
 *
 * @property universeClient the GUI independent client
 * @property defaultGdxSettings the default gdx settings
 * @property exit call this to exit app
 */
class RelativitizationGame(
    val universeClient: UniverseClient,
    private val defaultGdxSettings: GdxSettings,
    val exit: () -> Unit,
) : Game() {

    val gdxSettings: GdxSettings = GdxSettings.loadOrDefault(
        universeClient.universeClientSettings.programDir,
        defaultGdxSettings,
    )

    val onGdxSettingsChangeFunctionList: MutableList<() -> Unit> = mutableListOf()

    // call when gdx setting is changed
    val changeGdxSettings: () -> Unit = {
        onGdxSettingsChangeFunctionList.forEach { it() }
    }

    val assets: Assets = Assets(gdxSettings)

    private lateinit var backgroundMusic: Music

    var isGameStarted: Boolean = false

    // Avoid infinite looping, exit() may call dispose()
    private var isGameDisposed = false

    override fun create() {
        Gdx.input.setCatchKey(Input.Keys.BACK, true)
        Gdx.graphics.isContinuousRendering = gdxSettings.isContinuousRendering

        assets.loadAll()

        restoreSize()
        startMusic()

        setScreen(MainMenuScreen(this))
    }

    override fun dispose() {
        if (!isGameDisposed) {
            isGameDisposed = true
            logger.debug("Stopping game")
            clear()
            exit()
            backgroundMusic.stop()
            assets.dispose()
            logger.debug("Game stopped")
        }
    }


    fun restoreSize() {
        if (Gdx.app.type == Application.ApplicationType.Desktop &&
            gdxSettings.windowsWidth > 39 &&
            gdxSettings.windowsHeight > 39
        ) {
            Gdx.graphics.setWindowedMode(gdxSettings.windowsWidth, gdxSettings.windowsHeight)

            if (Gdx.graphics.width != gdxSettings.windowsWidth ||
                Gdx.graphics.height != gdxSettings.windowsHeight
            ) {
                logger.debug(
                    "Cannot adjust the width and height to the value in settings," +
                            "store the current value"
                )

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
    }

    fun clear() {
        clearOnChangeFunctionList()
    }

    fun cleanSettings() {
        val gdxSettingsFile = File(
            universeClient.universeClientSettings.programDir + "/GdxSettings.json"
        )
        val generateSettingsFile = File(
        universeClient.universeClientSettings.programDir + "/GenerateSettings.json"
        )

        if (gdxSettingsFile.delete()) {
            logger.debug("Deleted: ${gdxSettingsFile.name}")
        } else {
            logger.debug("Delete failed: ${gdxSettingsFile.name}")
        }

        if (generateSettingsFile.delete()) {
            logger.debug("Deleted: ${generateSettingsFile.name}")
        } else {
            logger.debug("Delete failed: ${generateSettingsFile.name}")
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}