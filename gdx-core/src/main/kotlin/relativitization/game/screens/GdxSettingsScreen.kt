package relativitization.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class GdxSettingsScreen(val game: RelativitizationGame, val inGame: Boolean) : TableScreen(game.assets) {
    private val gdxSettings = game.gdxSettings

    override fun show() {
        super.show()

        root.add(createGdxSettingsScrollPane())

        root.row().space(10f)

        // Can't cancel, going to apply anyway
        val applyButton = createTextButton(
            "Apply",
            gdxSettings.normalFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            game.changeGdxSettings()
            game.restoreSize()
            game.restartMusic()
            if (inGame) {
                game.screen = GameScreen(game)
            } else {
                game.screen = MainMenuScreen(game)
            }
        }

        root.add(applyButton)

        root.row().space(10f)

        root.add(createQuitTable())
    }

    private fun createQuitTable(): Table {
        val nestedTable: Table = Table()

        val confirmButton = createTextButton(
            "Confirm",
            gdxSettings.normalFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            runBlocking {
                game.universeClient.addToOnServerStatusChangeFunctionList { Gdx.app.exit() }
            }
        }

        val cancelButton = createTextButton(
            "Cancel",
            gdxSettings.normalFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            it.isVisible = false
            confirmButton.isVisible = false
        }

        val quitGameButton = createTextButton(
            "Quit game",
            gdxSettings.normalFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            confirmButton.isVisible = true
            cancelButton.isVisible = true
        }

        nestedTable.add(quitGameButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(confirmButton).space(10f)
        nestedTable.add(cancelButton)

        confirmButton.isVisible = false
        cancelButton.isVisible = false

        return nestedTable
    }

    private fun createGdxSettingsScrollPane(): ScrollPane {
        val table: Table = Table()

        val scrollPane: ScrollPane = createScrollPane(table)

        val gdxSettingsLabel = createLabel("Gdx Settings:", gdxSettings.hugeFontSize)

        table.add(gdxSettingsLabel).colspan(2).space(20f)

        table.row().space(10f)

        addGdxSettings(table)

        scrollPane.fadeScrollBars = false
        scrollPane.setFlickScroll(true)

        return scrollPane
    }

    private fun addGdxSettings(table: Table) {
        table.add(createLabel("Continuous rendering: ", gdxSettings.normalFontSize))
        val continuousRenderingCheckBox = createCheckBox(
            "",
            gdxSettings.continuousRendering,
            gdxSettings.normalFontSize
        ) { continuousRendering, _ ->
            gdxSettings.continuousRendering = continuousRendering
        }
        table.add(continuousRenderingCheckBox)

        table.row().space(10f)

        table.add(createLabel("Windows width: ", gdxSettings.normalFontSize))
        val widthTextField = createTextField(
            Gdx.graphics.width.toString(),
            gdxSettings.normalFontSize
        ) { width, _ ->
            try {
                gdxSettings.windowsWidth = width.toInt()
            } catch (e: NumberFormatException) {
                logger.error("Invalid windows width")
            }
        }
        table.add(widthTextField)

        table.row().space(10f)

        table.add(createLabel("Windows height: ", gdxSettings.normalFontSize))
        val heightTextField = createTextField(
            Gdx.graphics.height.toString(),
            gdxSettings.normalFontSize
        ) { width, _ ->
            try {
                gdxSettings.windowsWidth = width.toInt()
            } catch (e: NumberFormatException) {
                logger.error("Invalid windows height")
            }
        }
        table.add(heightTextField)

        table.row().space(10f)

        table.add(createLabel("Music volume: ", gdxSettings.normalFontSize))
        val musicVolumeSlider = createSlider(
            0.0f,
            1.0f,
            0.01f,
            gdxSettings.musicVolume,
            false
        ) { musicVolume, _ ->
            gdxSettings.musicVolume = musicVolume
        }
        table.add(musicVolumeSlider)


        table.row().space(10f)

        table.add(createLabel("Sound effect volume: ", gdxSettings.normalFontSize))
        val soundEffectVolumeSlider = createSlider(
            0.0f,
            1.0f,
            0.01f,
            gdxSettings.soundEffectsVolume,
            false
        ) { soundEffectVolume, _ ->
            gdxSettings.soundEffectsVolume = soundEffectVolume
        }
        table.add(soundEffectVolumeSlider)


        table.row().space(10f)

        table.add(createLabel("Small font size: ", gdxSettings.normalFontSize))
        val smallFontSizeSelectBox = createSelectBox(
            (8..80).toList(),
            gdxSettings.smallFontSize,
            gdxSettings.normalFontSize
        ) { smallFontSize, _ ->
            gdxSettings.smallFontSize = smallFontSize
        }
        table.add(smallFontSizeSelectBox)

        table.row().space(10f)

        table.add(createLabel("Normal font size: ", gdxSettings.normalFontSize))
        val normalFontSizeSelectBox = createSelectBox(
            (8..80).toList(),
            gdxSettings.normalFontSize,
            gdxSettings.normalFontSize
        ) { normalFontSize, _ ->
            gdxSettings.normalFontSize = normalFontSize
        }
        table.add(normalFontSizeSelectBox)

        table.row().space(10f)

        table.add(createLabel("Big font size: ", gdxSettings.normalFontSize))
        val bigFontSizeSelectBox = createSelectBox(
            (8..80).toList(),
            gdxSettings.bigFontSize,
            gdxSettings.normalFontSize
        ) { bigFontSize, _ ->
            gdxSettings.bigFontSize = bigFontSize
        }
        table.add(bigFontSizeSelectBox)

        table.row().space(10f)

        table.add(createLabel("Huge font size: ", gdxSettings.normalFontSize))
        val hugeFontSizeSelectBox = createSelectBox(
            (8..80).toList(),
            gdxSettings.hugeFontSize,
            gdxSettings.normalFontSize
        ) { hugeFontSize, _ ->
            gdxSettings.hugeFontSize = hugeFontSize
        }
        table.add(hugeFontSizeSelectBox)

        table.row().space(10f)

        table.add(createLabel("Map Zoom factor: ", gdxSettings.normalFontSize))
        val mapZoomFactorTextField = createTextField(
            gdxSettings.mapZoomFactor.toString(),
            gdxSettings.normalFontSize
        ) { mapZoomFactor, _ ->
            try {
                gdxSettings.mapZoomFactor =  mapZoomFactor.toFloat()
            } catch (e: NumberFormatException) {
                logger.error("Invalid zoom factor")
            }
        }
        table.add(mapZoomFactorTextField)

        table.row().space(10f)

        table.add(createLabel("Image scale: ", gdxSettings.normalFontSize))
        val imageScaleTextField = createTextField(
            gdxSettings.imageScale.toString(),
            gdxSettings.normalFontSize
        ) { imageScale, _ ->
            try {
                gdxSettings.imageScale =  imageScale.toFloat()
            } catch (e: NumberFormatException) {
                logger.error("Invalid image scale")
            }
        }
        table.add(imageScaleTextField)
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}