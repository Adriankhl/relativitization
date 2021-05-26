package relativitization.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import org.apache.logging.log4j.LogManager
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class GdxSettingsScreen(val game: RelativitizationGame, val inGame: Boolean) : TableScreen(game.assets) {
    private val gdxSetting = game.gdxSetting

    override fun show() {
        super.show()

        root.add(createGdxSettingsScrollPane())

        root.row().space(10f)

        val applyButton = createTextButton(
            "Apply",
            gdxSetting.normalFontSize,
            gdxSetting.soundEffectsVolume,
        ) {
            if (inGame) {

            } else {
                game.screen = MainMenuScreen(game)
                game.restoreSize()
                game.restartMusic()
            }
        }

        root.add(applyButton)
    }

    private fun createGdxSettingsScrollPane(): ScrollPane {
        val table: Table = Table()

        val scrollPane: ScrollPane = createScrollPane(table)

        val gdxSettingsLabel = createLabel("Gdx Settings:", gdxSetting.hugeFontSize)

        table.add(gdxSettingsLabel).colspan(2).space(20f)

        table.row().space(10f)

        addGdxSettings(table)

        scrollPane.fadeScrollBars = false
        scrollPane.setFlickScroll(true)

        return scrollPane
    }

    private fun addGdxSettings(table: Table) {
        table.add(createLabel("Continuous rendering: ", gdxSetting.normalFontSize))
        val continuousRenderingCheckBox = createCheckBox(
            "",
            gdxSetting.continuousRendering,
            gdxSetting.normalFontSize
        ) { continuousRendering, _ ->
            gdxSetting.continuousRendering = continuousRendering
        }
        table.add(continuousRenderingCheckBox)

        table.row().space(10f)

        table.add(createLabel("Windows width: ", gdxSetting.normalFontSize))
        val widthTextField = createTextField(
            Gdx.graphics.width.toString(),
            gdxSetting.normalFontSize
        ) { width, _ ->
            try {
                gdxSetting.windowsWidth = width.toInt()
            } catch (e: NumberFormatException) {
                logger.error("Invalid windows width")
            }
        }
        table.add(widthTextField)

        table.row().space(10f)

        table.add(createLabel("Windows height: ", gdxSetting.normalFontSize))
        val heightTextField = createTextField(
            Gdx.graphics.height.toString(),
            gdxSetting.normalFontSize
        ) { width, _ ->
            try {
                gdxSetting.windowsWidth = width.toInt()
            } catch (e: NumberFormatException) {
                logger.error("Invalid windows height")
            }
        }
        table.add(heightTextField)

        table.row().space(10f)

        table.add(createLabel("Music volume: ", gdxSetting.normalFontSize))
        val musicVolumeSlider = createSlider(
            0.0f,
            1.0f,
            0.01f,
            gdxSetting.musicVolume,
            false
        ) { musicVolume, _ ->
            gdxSetting.musicVolume = musicVolume
        }
        table.add(musicVolumeSlider)


        table.row().space(10f)

        table.add(createLabel("Sound effect volume: ", gdxSetting.normalFontSize))
        val soundEffectVolumeSlider = createSlider(
            0.0f,
            1.0f,
            0.01f,
            gdxSetting.soundEffectsVolume,
            false
        ) { soundEffectVolume, _ ->
            gdxSetting.soundEffectsVolume = soundEffectVolume
        }
        table.add(soundEffectVolumeSlider)


        table.row().space(10f)

        table.add(createLabel("Small font size: ", gdxSetting.normalFontSize))
        val smallFontSizeSelectBox = createSelectBox(
            (8..80).toList(),
            gdxSetting.smallFontSize,
            gdxSetting.normalFontSize
        ) { smallFontSize, _ ->
            gdxSetting.smallFontSize = smallFontSize
        }
        table.add(smallFontSizeSelectBox)

        table.row().space(10f)

        table.add(createLabel("Normal font size: ", gdxSetting.normalFontSize))
        val normalFontSizeSelectBox = createSelectBox(
            (8..80).toList(),
            gdxSetting.normalFontSize,
            gdxSetting.normalFontSize
        ) { normalFontSize, _ ->
            gdxSetting.normalFontSize = normalFontSize
        }
        table.add(normalFontSizeSelectBox)

        table.row().space(10f)

        table.add(createLabel("Big font size: ", gdxSetting.normalFontSize))
        val bigFontSizeSelectBox = createSelectBox(
            (8..80).toList(),
            gdxSetting.bigFontSize,
            gdxSetting.normalFontSize
        ) { bigFontSize, _ ->
            gdxSetting.bigFontSize = bigFontSize
        }
        table.add(bigFontSizeSelectBox)

        table.row().space(10f)

        table.add(createLabel("Huge font size: ", gdxSetting.normalFontSize))
        val hugeFontSizeSelectBox = createSelectBox(
            (8..80).toList(),
            gdxSetting.hugeFontSize,
            gdxSetting.normalFontSize
        ) { hugeFontSize, _ ->
            gdxSetting.hugeFontSize = hugeFontSize
        }
        table.add(hugeFontSizeSelectBox)
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}