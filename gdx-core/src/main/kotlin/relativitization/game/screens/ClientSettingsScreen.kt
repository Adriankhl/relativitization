package relativitization.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import kotlinx.coroutines.runBlocking
import relativitization.game.Language
import relativitization.game.RelativitizationGame
import relativitization.game.components.upper.UpperInfo
import relativitization.game.utils.Assets
import relativitization.game.utils.TableScreen
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.reflect.full.primaryConstructor

class ClientSettingsScreen(
    val game: RelativitizationGame,
    private val inGame: Boolean
) : TableScreen(game.assets) {
    private val gdxSettings = game.gdxSettings
    private val universeClientSettings = game.universeClient.universeClientSettings

    override fun show() {
        super.show()

        root.add(createClientSettingsScrollPane()).grow()

        root.row().space(10f)

        // Can't cancel, going to apply anyway
        val applyButton = createTextButton(
            "Apply",
            gdxSettings.normalFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            game.gdxSettings.save(game.universeClient.universeClientSettings.programDir)
            game.assets.updateTranslationBundle(game.gdxSettings)
            game.changeGdxSettings()
            game.restoreSize()
            game.restartMusic()
            if (inGame) {
                game.screen = GameScreen(game)
                dispose()
            } else {
                game.screen = MainMenuScreen(game)
                dispose()
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

    private fun createClientSettingsScrollPane(): ScrollPane {
        val table: Table = Table()

        val scrollPane: ScrollPane = createScrollPane(table)

        val gdxSettingsLabel = createLabel("Gdx settings:", gdxSettings.hugeFontSize)

        table.add(gdxSettingsLabel).colspan(2).space(20f)

        table.row().space(10f)

        addGdxSettings(table)

        table.row().space(20f)

        val universeClientSettingsLabel = createLabel(
            "Universe client settings:",
            gdxSettings.hugeFontSize
        )

        table.add(universeClientSettingsLabel).colspan(2).space(20f)

        table.row().space(10f)

        addUniverseClientSettings(table)

        table.row().space(10f)

        // Add empty space for Android keyboard input
        val emptyLabel = createLabel("", gdxSettings.smallFontSize)
        emptyLabel.height = Gdx.graphics.height.toFloat()
        table.add(emptyLabel).minHeight(Gdx.graphics.height.toFloat())


        scrollPane.fadeScrollBars = false
        scrollPane.setClamp(true)
        scrollPane.setOverscroll(false, false)

        return scrollPane
    }

    private fun addGdxSettings(table: Table) {
        table.add(
            createLabel(
                "Continuous rendering: ",
                gdxSettings.normalFontSize
            )
        )
        val continuousRenderingCheckBox = createCheckBox(
            "",
            gdxSettings.isContinuousRendering,
            gdxSettings.normalFontSize
        ) { continuousRendering, _ ->
            gdxSettings.isContinuousRendering = continuousRendering
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
            min = 0.0f,
            max = 1.0f,
            stepSize = 0.01f,
            default = gdxSettings.musicVolume,
            scale = gdxSettings.imageScale,
            vertical = false,
        ) { musicVolume, _ ->
            gdxSettings.musicVolume = musicVolume
        }
        table.add(musicVolumeSlider)


        table.row().space(10f)

        table.add(createLabel("Sound effect volume: ", gdxSettings.normalFontSize))
        val soundEffectVolumeSlider = createSlider(
            min = 0.0f,
            max = 1.0f,
            stepSize = 0.01f,
            default = gdxSettings.soundEffectsVolume,
            scale = gdxSettings.imageScale,
            vertical = false,
        ) { soundEffectVolume, _ ->
            gdxSettings.soundEffectsVolume = soundEffectVolume
        }
        table.add(soundEffectVolumeSlider)


        table.row().space(10f)

        table.add(createLabel("Small font size: ", gdxSettings.normalFontSize))
        val smallFontSizeSelectBox = createSelectBox(
            Assets.fontSizeList,
            gdxSettings.smallFontSize,
            gdxSettings.normalFontSize
        ) { smallFontSize, _ ->
            gdxSettings.smallFontSize = smallFontSize
        }
        table.add(smallFontSizeSelectBox)

        table.row().space(10f)

        table.add(createLabel("Normal font size: ", gdxSettings.normalFontSize))
        val normalFontSizeSelectBox = createSelectBox(
            Assets.fontSizeList,
            gdxSettings.normalFontSize,
            gdxSettings.normalFontSize
        ) { normalFontSize, _ ->
            gdxSettings.normalFontSize = normalFontSize
        }
        table.add(normalFontSizeSelectBox)

        table.row().space(10f)

        table.add(createLabel("Big font size: ", gdxSettings.normalFontSize))
        val bigFontSizeSelectBox = createSelectBox(
            Assets.fontSizeList,
            gdxSettings.bigFontSize,
            gdxSettings.normalFontSize
        ) { bigFontSize, _ ->
            gdxSettings.bigFontSize = bigFontSize
        }
        table.add(bigFontSizeSelectBox)

        table.row().space(10f)

        table.add(createLabel("Huge font size: ", gdxSettings.normalFontSize))
        val hugeFontSizeSelectBox = createSelectBox(
            Assets.fontSizeList,
            gdxSettings.hugeFontSize,
            gdxSettings.normalFontSize
        ) { hugeFontSize, _ ->
            gdxSettings.hugeFontSize = hugeFontSize
        }
        table.add(hugeFontSizeSelectBox)

        table.row().space(10f)

        table.add(createLabel("Map zoom factor: ", gdxSettings.normalFontSize))
        val mapZoomFactorTextField = createTextField(
            gdxSettings.mapZoomFactor.toString(),
            gdxSettings.normalFontSize
        ) { mapZoomFactor, _ ->
            try {
                gdxSettings.mapZoomFactor = mapZoomFactor.toFloat()
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
                gdxSettings.imageScale = imageScale.toFloat()
            } catch (e: NumberFormatException) {
                logger.error("Invalid image scale")
            }
        }
        table.add(imageScaleTextField)


        table.row().space(10f)

        table.add(createLabel("Knowledge map zoom factor: ", gdxSettings.normalFontSize))
        val knowledgeMapZoomTextField = createTextField(
            gdxSettings.mapZoomFactor.toString(),
            gdxSettings.normalFontSize
        ) { knowledgeMapZoomFactor, _ ->
            try {
                gdxSettings.knowledgeMapZoomRelativeToFullMap = knowledgeMapZoomFactor.toFloat()
            } catch (e: NumberFormatException) {
                logger.error("Invalid knowledge map zoom")
            }
        }
        table.add(knowledgeMapZoomTextField)

        table.row().space(10f)

        table.add(createLabel("Knowledge map icon zoom: ", gdxSettings.normalFontSize))
        val knowledgeMapIconZoomTextField = createTextField(
            gdxSettings.imageScale.toString(),
            gdxSettings.normalFontSize
        ) { knowledgeMapIconZoom, _ ->
            try {
                gdxSettings.knowledgeMapProjectIconZoom = knowledgeMapIconZoom.toFloat()
            } catch (e: NumberFormatException) {
                logger.error("Invalid knowledge map icon zoom")
            }
        }
        table.add(knowledgeMapIconZoomTextField)

        table.row().space(10f)

        table.add(createLabel("Show info: ", gdxSettings.normalFontSize))
        val showingInfoCheckBox = createCheckBox(
            "",
            gdxSettings.showingInfo,
            gdxSettings.normalFontSize
        ) { showingInfo, _ ->
            gdxSettings.showingInfo = showingInfo
        }
        table.add(showingInfoCheckBox)

        table.row().space(10f)

        table.add(createLabel("WorldMap and Info split: ", gdxSettings.normalFontSize))
        val worldMapAndInfoSplitAmountTextField = createTextField(
            gdxSettings.worldMapAndInfoSplitAmount.toString(),
            gdxSettings.normalFontSize
        ) { worldMapAndInfoSplitAmount, _ ->
            try {
                gdxSettings.worldMapAndInfoSplitAmount = worldMapAndInfoSplitAmount.toFloat()
            } catch (e: NumberFormatException) {
                logger.error("Invalid worldMapAndInfoSplitAmount")
            }
        }
        table.add(worldMapAndInfoSplitAmountTextField)

        table.row().space(10f)

        table.add(createLabel("Show bottom command: ", gdxSettings.normalFontSize))
        val showingBottomCommand = createCheckBox(
            "",
            gdxSettings.showingBottomCommand,
            gdxSettings.normalFontSize
        ) { showingBottomCommand, _ ->
            gdxSettings.showingBottomCommand = showingBottomCommand
        }
        table.add(showingBottomCommand)

        table.row().space(10f)

        table.add(createLabel("Info and command split: ", gdxSettings.normalFontSize))
        val upperInfoAndBottomCommandSplitAmountTextField = createTextField(
            gdxSettings.upperInfoAndBottomCommandSplitAmount.toString(),
            gdxSettings.normalFontSize
        ) { upperInfoAndBottomCommandSplitAmount, _ ->
            try {
                gdxSettings.upperInfoAndBottomCommandSplitAmount =
                    upperInfoAndBottomCommandSplitAmount.toFloat()
            } catch (e: NumberFormatException) {
                logger.error("Invalid worldMapAndInfoSplitAmount")
            }
        }
        table.add(upperInfoAndBottomCommandSplitAmountTextField)

        table.row().space(10f)

        table.add(createLabel("Showing upper info: ", gdxSettings.normalFontSize))
        val showingUpperInfoSelectBox = createSelectBox(
            UpperInfo::class.sealedSubclasses.map {
                it.primaryConstructor!!.call(game)
            }.sortedBy {
                it.infoPriority
            }.map {
                it.infoName
            },
            gdxSettings.showingUpperInfo,
            gdxSettings.normalFontSize
        ) { showingUpperInfo, _ ->
            gdxSettings.showingUpperInfo = showingUpperInfo
        }
        table.add(showingUpperInfoSelectBox)

        table.row().space(10f)

        table.add(createLabel("Language: ", gdxSettings.normalFontSize))
        val languageSelectBox = createSelectBox(
            Language.values().toList(),
            gdxSettings.language,
            gdxSettings.normalFontSize
        ) { language, _ ->
            gdxSettings.language = language
        }
        table.add(languageSelectBox)
    }

    private fun addUniverseClientSettings(table: Table) {
        table.add(
            createLabel(
                "Max. stored data:  ",
                gdxSettings.normalFontSize
            )
        )

        val maxStoredUniverseData3DAtPlayerSelectBox = createSelectBox(
            listOf(100000000) + (5..50 step 5).toList(),
            universeClientSettings.maxStoredUniverseData3DAtPlayer,
            gdxSettings.normalFontSize,
        ) { i, _ ->
            universeClientSettings.maxStoredUniverseData3DAtPlayer = i
        }
        table.add(maxStoredUniverseData3DAtPlayerSelectBox)

        table.row().space(10f)

        table.add(
            createLabel(
                "Auto-confirm current command: ",
                gdxSettings.normalFontSize
            )
        )
        val autoConfirmCurrentCommandCheckBox = createCheckBox(
            "",
            universeClientSettings.autoConfirmCurrentCommand,
            gdxSettings.normalFontSize,
        ) { autoConfirmCurrentCommand, _ ->
            universeClientSettings.autoConfirmCurrentCommand = autoConfirmCurrentCommand
        }
        table.add(autoConfirmCurrentCommandCheckBox)

        table.row().space(10f)

        table.add(
            createLabel(
                "Http request timeout (in ms): ",
                gdxSettings.normalFontSize
            )
        )

        val httpRequestTimeoutTextField = createTextField(
            universeClientSettings.httpRequestTimeout.toString(),
            gdxSettings.normalFontSize
        ) { httpRequestTimeOut, _ ->
            try {
                universeClientSettings.httpRequestTimeout = httpRequestTimeOut.toLong()
            } catch (e: NumberFormatException) {
                logger.error("Invalid httpRequestTimeout")
            }
        }
        table.add(httpRequestTimeoutTextField)

        table.row().space(10f)

        table.add(
            createLabel(
                "Http connect timeout (in ms): ",
                gdxSettings.normalFontSize
            )
        )

        val httpConnectTimeoutTextField = createTextField(
            universeClientSettings.httpConnectTimeout.toString(),
            gdxSettings.normalFontSize
        ) { httpConnectTimeOut, _ ->
            try {
                universeClientSettings.httpConnectTimeout = httpConnectTimeOut.toLong()
            } catch (e: NumberFormatException) {
                logger.error("Invalid httpConnectTimeout")
            }
        }
        table.add(httpConnectTimeoutTextField)

        table.row().space(10f)


    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}