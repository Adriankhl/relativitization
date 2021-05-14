package relativitization.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen
import relativitization.universe.generate.GenerateUniverse

class NewUniverseScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    val gdxSetting = game.gdxSetting

    override fun show() {
        super.show()

        root.add(createGenerateSettingsScrollPane())

        root.row().space(10f)

        // Add Generate button
        val generateFailLabel = createLabel("", gdxSetting.normalFontSize)
        val generateButton = createTextButton("Generate", gdxSetting.buttonFontSize) {
            if (GenerateUniverse.isSettingValid(game.universeClient.generateSettings)) {
                logger.info("Generate settings: " + game.universeClient.generateSettings)
                runBlocking {
                    game.universeClient.httpPostNewUniverse()
                }
            } else {
                generateFailLabel.setText("Generate universe fail, some setting is wrong")
            }
        }

        root.add(generateButton)
        root.row().space(10f)
        root.add(generateFailLabel)
    }

    /**
     * Create scroll pane for all generate universe setting
     */
    private fun createGenerateSettingsScrollPane(): ScrollPane {
        val table = Table()

        val generateSettingLabel = createLabel("Generate Universe Settings:", gdxSetting.bigFontSIze)

        table.add(generateSettingLabel).colspan(2).space(20f)
        table.row().space(10f)

        addGenerateSettings(table)
        table.row().space(10f)

        addUniverseSettings(table)

        val scrollPane: ScrollPane = createScrollPane(table)

        scrollPane.fadeScrollBars = false;
        scrollPane.setFlickScroll(false);

        return scrollPane
    }

    /**
     * Add generate method and various number setting except universe setting
     */
    private fun addGenerateSettings(table: Table) {
        table.add(createLabel("Generate Method: ", gdxSetting.normalFontSize))
        val generateMethodSelectBox = createSelectBox(
            GenerateUniverse.generateMethodMap.keys.toList(),
            game.universeClient.generateSettings.generateMethod,
            gdxSetting.normalFontSize,
        ) {
            game.universeClient.generateSettings.generateMethod = it
        }
        table.add(generateMethodSelectBox)

        table.row().space(10f)

        table.add(createLabel("Total number of AI/human player: ", gdxSetting.normalFontSize))
        val numPlayerSelectBox = createSelectBox(
            (1..1000).toList(),
            game.universeClient.generateSettings.numPlayer,
            gdxSetting.normalFontSize,
        ) {
            game.universeClient.generateSettings.numPlayer = it
        }
        table.add(numPlayerSelectBox)

        table.row().space(10f)

        table.add(createLabel("Total number of human player: ", gdxSetting.normalFontSize))
        val numHumanPlayerSelectBox = createSelectBox(
            (1..1000).toList(),
            game.universeClient.generateSettings.numHumanPlayer,
            gdxSetting.normalFontSize,
        ) {
            game.universeClient.generateSettings.numHumanPlayer = it
        }
        table.add(numHumanPlayerSelectBox)

        table.row().space(10f)

        table.add(createLabel("Total number of extra stellar system: ", gdxSetting.normalFontSize))
        val numExtraStellarSystemSelectBox = createSelectBox(
            (1..1000).toList(),
            game.universeClient.generateSettings.numExtraStellarSystem,
            gdxSetting.normalFontSize,
        ) {
            game.universeClient.generateSettings.numExtraStellarSystem = it
        }
        table.add(numExtraStellarSystemSelectBox)

    }

    /**
     * Add Universe setting
     */
    private fun addUniverseSettings(table: Table) {
        table.add(createLabel("Universe name: ", gdxSetting.normalFontSize))
        val universeNameTextField = createTextField(
            game.universeClient.generateSettings.universeSettings.universeName,
            gdxSetting.normalFontSize
        ) {
            game.universeClient.generateSettings.universeSettings.universeName = it
        }
        table.add(universeNameTextField)

        table.row().space(10f)

        table.add(createLabel("Speed of light: ", gdxSetting.normalFontSize))
        val speedOfLightSelectBox = createSelectBox(
            (1..10).toList(),
            game.universeClient.generateSettings.universeSettings.speedOfLight,
            gdxSetting.normalFontSize
        ) {
            game.universeClient.generateSettings.universeSettings.speedOfLight = it
        }
        table.add(speedOfLightSelectBox)

        table.row().space(10f)

        table.add(createLabel("Universe time dimension: ", gdxSetting.normalFontSize))
        val tDimSelectBox = createSelectBox(
            (1..50).toList(),
            game.universeClient.generateSettings.universeSettings.tDim,
            gdxSetting.normalFontSize
        ) {
            game.universeClient.generateSettings.universeSettings.tDim = it
        }
        table.add(tDimSelectBox)

        table.row().space(10f)

        table.add(createLabel("Universe x dimension: ", gdxSetting.normalFontSize))
        val xDimSelectBox = createSelectBox(
            (1..50).toList(),
            game.universeClient.generateSettings.universeSettings.xDim,
            gdxSetting.normalFontSize
        ) {
            game.universeClient.generateSettings.universeSettings.xDim = it
        }
        table.add(xDimSelectBox)

        table.row().space(10f)

        table.add(createLabel("Universe y dimension: ", gdxSetting.normalFontSize))
        val yDimSelectBox = createSelectBox(
            (1..50).toList(),
            game.universeClient.generateSettings.universeSettings.yDim,
            gdxSetting.normalFontSize
        ) {
            game.universeClient.generateSettings.universeSettings.yDim = it
        }
        table.add(yDimSelectBox)

        table.row().space(10f)

        table.add(createLabel("Universe z dimension: ", gdxSetting.normalFontSize))
        val zDimSelectBox = createSelectBox(
            (1..50).toList(),
            game.universeClient.generateSettings.universeSettings.zDim,
            gdxSetting.normalFontSize
        ) {
            game.universeClient.generateSettings.universeSettings.zDim = it
        }
        table.add(zDimSelectBox)

        table.row().space(10f)

        table.add(createLabel("Player after image duration: ", gdxSetting.normalFontSize))
        val playerAfterImageSelectBox = createSelectBox(
            (4..10).toList(),
            game.universeClient.generateSettings.universeSettings.playerAfterImageDuration,
            gdxSetting.normalFontSize
        ) {
            game.universeClient.generateSettings.universeSettings.playerAfterImageDuration = it
        }
        table.add(playerAfterImageSelectBox)

        table.row().space(10f)

        table.add(
            createLabel(
                "Player trajectory length (must be greater than after image ducation): ",
                gdxSetting.normalFontSize
            )
        )
        val playerHistoricalInt4DLengthSelectBox = createSelectBox(
            (4..10).toList(),
            game.universeClient.generateSettings.universeSettings.playerHistoricalInt4DLength,
            gdxSetting.normalFontSize
        ) {
            game.universeClient.generateSettings.universeSettings.playerHistoricalInt4DLength = it
        }
        table.add(playerHistoricalInt4DLengthSelectBox)

        table.row().space(10f)
    }

    companion object {
        private val logger = LogManager.getLogger()

    }
}