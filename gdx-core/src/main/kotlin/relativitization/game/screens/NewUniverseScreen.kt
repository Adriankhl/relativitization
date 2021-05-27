package relativitization.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen
import relativitization.universe.data.commands.Command
import relativitization.universe.generate.GenerateUniverse
import relativitization.universe.mechanisms.MechanismCollection

class NewUniverseScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    private val gdxSetting = game.gdxSetting

    override fun show() {
        super.show()

        root.add(createGenerateSettingsScrollPane())

        root.row().space(20f)

        // Add Generate button
        val generateFailLabel = createLabel("", gdxSetting.normalFontSize)
        val generateButton = createTextButton("Generate", gdxSetting.bigFontSize, gdxSetting.soundEffectsVolume) {
            if (GenerateUniverse.isSettingValid(game.universeClient.generateSettings)) {
                logger.info("Generate settings: " + game.universeClient.generateSettings)
                runBlocking {
                    val httpCode = game.universeClient.httpPostNewUniverse()
                    if (httpCode == HttpStatusCode.OK) {
                        game.screen = ServerSettingsScreen(game)
                        dispose()
                    } else {
                        generateFailLabel.setText("Generate universe fail, http code: $httpCode")
                    }
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

        val generateSettingLabel = createLabel("Generate Universe Settings:", gdxSetting.hugeFontSize)

        table.add(generateSettingLabel).colspan(2).space(20f)
        table.row().space(10f)

        addGenerateSettings(table)
        table.row().space(10f)

        addUniverseSettings(table)

        val scrollPane: ScrollPane = createScrollPane(table)

        scrollPane.fadeScrollBars = false
        scrollPane.setFlickScroll(true)

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
        ) { method, _ ->
            game.universeClient.generateSettings.generateMethod = method
        }
        table.add(generateMethodSelectBox)

        table.row().space(10f)

        table.add(createLabel("Pick game mechanisms: ", gdxSetting.normalFontSize))
        val mechanismSelectBox = createSelectBox(
            MechanismCollection.mechanismProcessNameList,
            game.universeClient.generateSettings.universeSettings.mechanismCollectionName,
            gdxSetting.normalFontSize,
        ) { mechanismCollectionName, _ ->
            game.universeClient.generateSettings.universeSettings.mechanismCollectionName = mechanismCollectionName
        }
        table.add(mechanismSelectBox)

        table.row().space(10f)

        table.add(createLabel("Pick available commands: ", gdxSetting.normalFontSize))
        val commandSelectBox = createSelectBox(
            Command.commandCollectionList,
            game.universeClient.generateSettings.universeSettings.commandCollectionName,
            gdxSetting.normalFontSize,
        ) { commandCollectionName, _ ->
            game.universeClient.generateSettings.universeSettings.commandCollectionName = commandCollectionName
        }
        table.add(commandSelectBox)

        table.row().space(10f)


        table.add(createLabel("Total number of AI/human player: ", gdxSetting.normalFontSize))
        val numPlayerSelectBox = createSelectBox(
            (1..1000).toList(),
            game.universeClient.generateSettings.numPlayer,
            gdxSetting.normalFontSize,
        ) { numPlayer, _ ->
            game.universeClient.generateSettings.numPlayer = numPlayer
        }
        table.add(numPlayerSelectBox)

        table.row().space(10f)

        table.add(createLabel("Total number of human player: ", gdxSetting.normalFontSize))
        val numHumanPlayerSelectBox = createSelectBox(
            (1..1000).toList(),
            game.universeClient.generateSettings.numHumanPlayer,
            gdxSetting.normalFontSize,
        ) { numHumanPlayer, _ ->
            game.universeClient.generateSettings.numHumanPlayer = numHumanPlayer
        }
        table.add(numHumanPlayerSelectBox)

        table.row().space(10f)

        table.add(createLabel("Total number of extra stellar system: ", gdxSetting.normalFontSize))
        val numExtraStellarSystemSelectBox = createSelectBox(
            (1..1000).toList(),
            game.universeClient.generateSettings.numExtraStellarSystem,
            gdxSetting.normalFontSize,
        ) { numExtraStellarSystem, _ ->
            game.universeClient.generateSettings.numExtraStellarSystem = numExtraStellarSystem
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
        ) { name, _ ->
            game.universeClient.generateSettings.universeSettings.universeName = name
        }
        table.add(universeNameTextField)

        table.row().space(10f)

        table.add(createLabel("Speed of light: ", gdxSetting.normalFontSize))
        val speedOfLightTextField = createTextField(
            game.universeClient.generateSettings.universeSettings.speedOfLight.toString(),
            gdxSetting.normalFontSize
        ) { speedOfLight, _ ->
            try {
                game.universeClient.generateSettings.universeSettings.speedOfLight = speedOfLight.toDouble()
            } catch (e: NumberFormatException) {
                logger.error("Invalid speed of light")
            }
        }
        table.add(speedOfLightTextField)

        table.row().space(10f)

        table.add(createLabel("Universe time dimension: ", gdxSetting.normalFontSize))
        val tDimSelectBox = createSelectBox(
            (1..50).toList(),
            game.universeClient.generateSettings.universeSettings.tDim,
            gdxSetting.normalFontSize
        ) { tDim, _ ->
            game.universeClient.generateSettings.universeSettings.tDim = tDim
        }
        table.add(tDimSelectBox)

        table.row().space(10f)

        table.add(createLabel("Universe x dimension: ", gdxSetting.normalFontSize))
        val xDimSelectBox = createSelectBox(
            (1..50).toList(),
            game.universeClient.generateSettings.universeSettings.xDim,
            gdxSetting.normalFontSize
        ) { xDim, _ ->
            game.universeClient.generateSettings.universeSettings.xDim = xDim
        }
        table.add(xDimSelectBox)

        table.row().space(10f)

        table.add(createLabel("Universe y dimension: ", gdxSetting.normalFontSize))
        val yDimSelectBox = createSelectBox(
            (1..50).toList(),
            game.universeClient.generateSettings.universeSettings.yDim,
            gdxSetting.normalFontSize
        ) { yDim, _ ->
            game.universeClient.generateSettings.universeSettings.yDim = yDim
        }
        table.add(yDimSelectBox)

        table.row().space(10f)

        table.add(createLabel("Universe z dimension: ", gdxSetting.normalFontSize))
        val zDimSelectBox = createSelectBox(
            (1..50).toList(),
            game.universeClient.generateSettings.universeSettings.zDim,
            gdxSetting.normalFontSize
        ) { zDim, _ ->
            game.universeClient.generateSettings.universeSettings.zDim = zDim
        }
        table.add(zDimSelectBox)

        table.row().space(10f)

        table.add(createLabel("Player after image duration: ", gdxSetting.normalFontSize))
        val playerAfterImageSelectBox = createSelectBox(
            (4..10).toList(),
            game.universeClient.generateSettings.universeSettings.playerAfterImageDuration,
            gdxSetting.normalFontSize
        ) { duration, _ ->
            game.universeClient.generateSettings.universeSettings.playerAfterImageDuration = duration
        }
        table.add(playerAfterImageSelectBox)

        table.row().space(10f)

        table.add(
            createLabel(
                "Player trajectory length (must be greater than after image duration): ",
                gdxSetting.normalFontSize
            )
        )
        val playerHistoricalInt4DLengthSelectBox = createSelectBox(
            (4..10).toList(),
            game.universeClient.generateSettings.universeSettings.playerHistoricalInt4DLength,
            gdxSetting.normalFontSize
        ) { length, _ ->
            game.universeClient.generateSettings.universeSettings.playerHistoricalInt4DLength = length
        }
        table.add(playerHistoricalInt4DLengthSelectBox)

        table.row().space(10f)
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}