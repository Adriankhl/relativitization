package relativitization.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen
import relativitization.universe.data.commands.CommandCollection
import relativitization.universe.generate.UniverseGenerationCollection
import relativitization.universe.mechanisms.MechanismCollection
import relativitization.universe.global.science.UniverseScienceDataProcessCollection
import relativitization.universe.utils.RelativitizationLogManager

class NewUniverseScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    private val gdxSettings = game.gdxSettings

    override fun show() {
        super.show()

        root.add(createGenerateSettingsScrollPane()).pad(20f).growX()

        root.row().space(20f)

        root.add(createButtonTable())
    }

    /**
     * Create table for generate button and cancel button
     */
    private fun createButtonTable(): Table {
        val nestedTable = Table()


        // Add Generate button
        val generateFailLabel = createLabel("", gdxSettings.normalFontSize)
        val generateButton = createTextButton("Generate", gdxSettings.bigFontSize, gdxSettings.soundEffectsVolume) {
            if (UniverseGenerationCollection.isSettingValid(game.universeClient.generateSettings)) {
                logger.info("Generate settings: " + game.universeClient.generateSettings)
                runBlocking {
                    val httpCode = game.universeClient.httpPostNewUniverse()
                    if (httpCode == HttpStatusCode.OK) {
                        // Save generate setting for the next generation
                        game.universeClient.generateSettings.save(
                            game.universeClient.universeClientSettings.programDir
                        )
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

        val cancelButton = createTextButton(
            "Cancel",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = MainMenuScreen(game)
        }
        nestedTable.add(generateButton).space(10f)
        nestedTable.add(cancelButton).space(10f)
        nestedTable.row().space(10f)
        nestedTable.add(generateFailLabel).colspan(2)

        return nestedTable
    }

    /**
     * Create scroll pane for all generate universe setting
     */
    private fun createGenerateSettingsScrollPane(): ScrollPane {
        val table = Table()

        val generateSettingLabel = createLabel("Generate Universe Settings:", gdxSettings.hugeFontSize)

        table.add(generateSettingLabel).colspan(2).space(20f)

        table.row().space(10f)

        addGenerateSettings(table)

        table.row().space(10f)

        addUniverseSettings(table)

        table.row().space(10f)

        // Add empty space for Android keyboard input
        val emptyLabel = createLabel("", gdxSettings.smallFontSize)
        emptyLabel.height = Gdx.graphics.height.toFloat()
        table.add(emptyLabel).minHeight(Gdx.graphics.height.toFloat())


        val scrollPane: ScrollPane = createScrollPane(table)

        scrollPane.fadeScrollBars = false
        scrollPane.setClamp(true)
        scrollPane.setOverscroll(false, false)

        return scrollPane
    }

    /**
     * Add generate method and various number setting except universe setting
     */
    private fun addGenerateSettings(table: Table) {
        table.add(createLabel("Generate Method: ", gdxSettings.normalFontSize))
        val generateMethodSelectBox = createSelectBox(
            UniverseGenerationCollection.generateMethodMap.keys.toList(),
            game.universeClient.generateSettings.generateMethod,
            gdxSettings.normalFontSize,
        ) { method, _ ->
            game.universeClient.generateSettings.generateMethod = method
        }
        table.add(generateMethodSelectBox)

        table.row().space(10f)

        table.add(createLabel("Pick game mechanisms: ", gdxSettings.normalFontSize))
        val mechanismSelectBox = createSelectBox(
            MechanismCollection.mechanismListMap.keys.toList(),
            game.universeClient.generateSettings.universeSettings.mechanismCollectionName,
            gdxSettings.normalFontSize,
        ) { mechanismCollectionName, _ ->
            game.universeClient.generateSettings.universeSettings.mechanismCollectionName = mechanismCollectionName
        }
        table.add(mechanismSelectBox)

        table.row().space(10f)

        table.add(createLabel("Pick available commands: ", gdxSettings.normalFontSize))
        val commandSelectBox = createSelectBox(
            CommandCollection.commandAvailabilityNameMap.keys.toList(),
            game.universeClient.generateSettings.universeSettings.commandCollectionName,
            gdxSettings.normalFontSize,
        ) { commandCollectionName, _ ->
            game.universeClient.generateSettings.universeSettings.commandCollectionName = commandCollectionName
        }
        table.add(commandSelectBox)

        table.row().space(10f)

        table.add(createLabel("Pick universe science data process: ", gdxSettings.normalFontSize))
        val universeScienceDataProcessSelectBox = createSelectBox(
            UniverseScienceDataProcessCollection.universeScienceDataProcessNameMap.keys.toList(),
            game.universeClient.generateSettings.universeSettings.universeScienceDataProcessCollectionName,
            gdxSettings.normalFontSize,
        ) { universeScienceDataProcessName, _ ->
            game.universeClient.generateSettings.universeSettings.universeScienceDataProcessCollectionName = universeScienceDataProcessName
        }
        table.add(universeScienceDataProcessSelectBox)

        table.row().space(10f)



        table.add(createLabel("Total number of AI/human player: ", gdxSettings.normalFontSize))
        val numPlayerSelectBox = createSelectBox(
            (1..1000).toList(),
            game.universeClient.generateSettings.numPlayer,
            gdxSettings.normalFontSize,
        ) { numPlayer, _ ->
            game.universeClient.generateSettings.numPlayer = numPlayer
        }
        table.add(numPlayerSelectBox)

        table.row().space(10f)

        table.add(createLabel("Total number of human player: ", gdxSettings.normalFontSize))
        val numHumanPlayerSelectBox = createSelectBox(
            (1..1000).toList(),
            game.universeClient.generateSettings.numHumanPlayer,
            gdxSettings.normalFontSize,
        ) { numHumanPlayer, _ ->
            game.universeClient.generateSettings.numHumanPlayer = numHumanPlayer
        }
        table.add(numHumanPlayerSelectBox)

        table.row().space(10f)

        table.add(createLabel("Total number of extra stellar system: ", gdxSettings.normalFontSize))
        val numExtraStellarSystemSelectBox = createSelectBox(
            (0..1000).toList(),
            game.universeClient.generateSettings.numExtraStellarSystem,
            gdxSettings.normalFontSize,
        ) { numExtraStellarSystem, _ ->
            game.universeClient.generateSettings.numExtraStellarSystem = numExtraStellarSystem
        }
        table.add(numExtraStellarSystemSelectBox)

    }

    /**
     * Add Universe setting
     */
    private fun addUniverseSettings(table: Table) {
        table.add(createLabel("Universe name: ", gdxSettings.normalFontSize))
        val universeNameTextField = createTextField(
            game.universeClient.generateSettings.universeSettings.universeName,
            gdxSettings.normalFontSize
        ) { name, _ ->
            game.universeClient.generateSettings.universeSettings.universeName = name
        }
        table.add(universeNameTextField)

        table.row().space(10f)

        table.add(createLabel("Speed of light: ", gdxSettings.normalFontSize))
        val speedOfLightTextField = createTextField(
            game.universeClient.generateSettings.universeSettings.speedOfLight.toString(),
            gdxSettings.normalFontSize
        ) { speedOfLight, _ ->
            try {
                game.universeClient.generateSettings.universeSettings.speedOfLight = speedOfLight.toDouble()
            } catch (e: NumberFormatException) {
                logger.error("Invalid speed of light")
            }
        }
        table.add(speedOfLightTextField)

        table.row().space(10f)

        table.add(createLabel("Universe time dimension: ", gdxSettings.normalFontSize))
        val tDimSelectBox = createSelectBox(
            (1..50).toList(),
            game.universeClient.generateSettings.universeSettings.tDim,
            gdxSettings.normalFontSize
        ) { tDim, _ ->
            game.universeClient.generateSettings.universeSettings.tDim = tDim
        }
        table.add(tDimSelectBox)

        table.row().space(10f)

        table.add(createLabel("Universe x dimension: ", gdxSettings.normalFontSize))
        val xDimSelectBox = createSelectBox(
            (1..50).toList(),
            game.universeClient.generateSettings.universeSettings.xDim,
            gdxSettings.normalFontSize
        ) { xDim, _ ->
            game.universeClient.generateSettings.universeSettings.xDim = xDim
        }
        table.add(xDimSelectBox)

        table.row().space(10f)

        table.add(createLabel("Universe y dimension: ", gdxSettings.normalFontSize))
        val yDimSelectBox = createSelectBox(
            (1..50).toList(),
            game.universeClient.generateSettings.universeSettings.yDim,
            gdxSettings.normalFontSize
        ) { yDim, _ ->
            game.universeClient.generateSettings.universeSettings.yDim = yDim
        }
        table.add(yDimSelectBox)

        table.row().space(10f)

        table.add(createLabel("Universe z dimension: ", gdxSettings.normalFontSize))
        val zDimSelectBox = createSelectBox(
            (1..50).toList(),
            game.universeClient.generateSettings.universeSettings.zDim,
            gdxSettings.normalFontSize
        ) { zDim, _ ->
            game.universeClient.generateSettings.universeSettings.zDim = zDim
        }
        table.add(zDimSelectBox)

        table.row().space(10f)

        table.add(createLabel("Player after image duration: ", gdxSettings.normalFontSize))
        val playerAfterImageSelectBox = createSelectBox(
            (4..10).toList(),
            game.universeClient.generateSettings.universeSettings.playerAfterImageDuration,
            gdxSettings.normalFontSize
        ) { duration, _ ->
            game.universeClient.generateSettings.universeSettings.playerAfterImageDuration = duration
        }
        table.add(playerAfterImageSelectBox)

        table.row().space(10f)

        table.add(
            createLabel(
                "Player trajectory length (must be greater than after image duration): ",
                gdxSettings.normalFontSize
            )
        )
        val playerHistoricalInt4DLengthSelectBox = createSelectBox(
            (4..10).toList(),
            game.universeClient.generateSettings.universeSettings.playerHistoricalInt4DLength,
            gdxSettings.normalFontSize
        ) { length, _ ->
            game.universeClient.generateSettings.universeSettings.playerHistoricalInt4DLength = length
        }
        table.add(playerHistoricalInt4DLengthSelectBox)

        table.row().space(10f)

        table.add(createLabel("Group edge length", gdxSettings.normalFontSize))
        val groupEdgeLengthTextField = createTextField(
            game.universeClient.generateSettings.universeSettings.groupEdgeLength.toString(),
            gdxSettings.normalFontSize
        ) { groupEdgeLength, _ ->
            try {
                game.universeClient.generateSettings.universeSettings.groupEdgeLength = groupEdgeLength.toDouble()
            } catch (e: NumberFormatException) {
                logger.error("Invalid group edge Length")
            }
        }

        table.add(groupEdgeLengthTextField)

        table.row().space(10f)
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}