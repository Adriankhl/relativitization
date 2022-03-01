package relativitization.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.CommandCollection
import relativitization.universe.maths.physics.Int3D
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.global.GlobalMechanismCollection
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.mechanisms.MechanismCollection
import relativitization.universe.utils.RelativitizationLogManager

class NewUniverseScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    private val gdxSettings = game.gdxSettings

    init {
        // Use default name
        game.universeClient.generateSettings.universeSettings.universeName = UniverseSettings().universeName
    }

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
        val generateButton = createTextButton(
            "Generate",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            if (GenerateUniverseMethodCollection.isSettingValid(game.universeClient.generateSettings)) {
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
            dispose()
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

        val generateSettingLabel =
            createLabel("Generate Universe Settings:", gdxSettings.hugeFontSize)

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
        table.add(
            createLabel(
                "Generate method: ",
                gdxSettings.normalFontSize
            )
        )

        val generateMethodSelectBox = createSelectBox(
            GenerateUniverseMethodCollection.generateMethodMap.keys.toList(),
            game.universeClient.generateSettings.generateMethod,
            gdxSettings.normalFontSize,
        ) { method, _ ->
            game.universeClient.generateSettings.generateMethod = method
        }
        table.add(generateMethodSelectBox)

        table.row().space(10f)

        table.add(createLabel("Pick game mechanisms: ", gdxSettings.normalFontSize))
        val mechanismSelectBox = createSelectBox(
            MechanismCollection.mechanismListsMap.keys.toList(),
            game.universeClient.generateSettings.universeSettings.mechanismCollectionName,
            gdxSettings.normalFontSize,
        ) { mechanismCollectionName, _ ->
            game.universeClient.generateSettings.universeSettings.mechanismCollectionName =
                mechanismCollectionName
        }
        table.add(mechanismSelectBox)

        table.row().space(10f)

        table.add(createLabel("Pick available commands: ", gdxSettings.normalFontSize))
        val commandSelectBox = createSelectBox(
            CommandCollection.commandAvailabilityNameMap.keys.toList(),
            game.universeClient.generateSettings.universeSettings.commandCollectionName,
            gdxSettings.normalFontSize,
        ) { commandCollectionName, _ ->
            game.universeClient.generateSettings.universeSettings.commandCollectionName =
                commandCollectionName
        }
        table.add(commandSelectBox)

        table.row().space(10f)

        table.add(createLabel("Pick global mechanics: ", gdxSettings.normalFontSize))
        val universeScienceDataProcessSelectBox = createSelectBox(
            GlobalMechanismCollection.globalMechanismListMap.keys.toList(),
            game.universeClient.generateSettings.universeSettings.globalMechanismCollectionName,
            gdxSettings.normalFontSize,
        ) { globalMechanismCollectionName, _ ->
            game.universeClient.generateSettings.universeSettings.globalMechanismCollectionName =
                globalMechanismCollectionName
        }
        table.add(universeScienceDataProcessSelectBox)

        table.row().space(10f)

        table.add(createLabel("Total number of AI + human player: ", gdxSettings.normalFontSize))
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
            game.universeClient.generateSettings.otherIntMap.getOrDefault("numExtraStellarSystem", 0),
            gdxSettings.normalFontSize,
        ) { numExtraStellarSystem, _ ->
            game.universeClient.generateSettings.otherIntMap["numExtraStellarSystem"] = numExtraStellarSystem
        }
        table.add(numExtraStellarSystemSelectBox)

    }

    /**
     * Add Universe setting
     */
    private fun addUniverseSettings(table: Table) {
        val tDimLabel = createLabel(
            game.universeClient.generateSettings.universeSettings.tDim.toString(),
            gdxSettings.normalFontSize
        )

        val playerAfterImageLabel = createLabel(
            game.universeClient.generateSettings.universeSettings.playerAfterImageDuration.toString(),
            gdxSettings.normalFontSize
        )

        val playerHistoricalInt4DLengthLabel = createLabel(
            game.universeClient.generateSettings.universeSettings.playerHistoricalInt4DLength.toString(),
            gdxSettings.normalFontSize
        )

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
                game.universeClient.generateSettings.universeSettings.speedOfLight =
                    speedOfLight.toDouble()

                // Change tDim
                val maxDelay: Int = Intervals.intDelay(
                    Int3D(0, 0, 0),
                    Int3D(
                        game.universeClient.generateSettings.universeSettings.xDim - 1,
                        game.universeClient.generateSettings.universeSettings.yDim - 1,
                        game.universeClient.generateSettings.universeSettings.zDim - 1
                    ),
                    game.universeClient.generateSettings.universeSettings.speedOfLight
                )

                game.universeClient.generateSettings.universeSettings.tDim = maxDelay + 1
                tDimLabel.setText(maxDelay + 1)

                // Change after image and history
                val minHistory: Int = Intervals.maxDelayAfterMove(
                    game.universeClient.generateSettings.universeSettings.speedOfLight
                )

                game.universeClient.generateSettings.universeSettings.playerAfterImageDuration = minHistory
                playerAfterImageLabel.setText(minHistory)

                game.universeClient.generateSettings.universeSettings.playerHistoricalInt4DLength = minHistory
                playerHistoricalInt4DLengthLabel.setText(minHistory)
            } catch (e: NumberFormatException) {
                logger.error("Invalid speed of light")
            }
        }
        table.add(speedOfLightTextField)

        table.row().space(10f)

        table.add(createLabel("Universe time dimension: ", gdxSettings.normalFontSize))

        table.add(tDimLabel)

        table.row().space(10f)

        table.add(createLabel("Universe x dimension: ", gdxSettings.normalFontSize))
        val xDimSelectBox = createSelectBox(
            (1..50).toList(),
            game.universeClient.generateSettings.universeSettings.xDim,
            gdxSettings.normalFontSize
        ) { xDim, _ ->
            game.universeClient.generateSettings.universeSettings.xDim = xDim

            // Change tDim
            val maxDelay: Int = Intervals.intDelay(
                Int3D(0, 0, 0),
                Int3D(
                    game.universeClient.generateSettings.universeSettings.xDim - 1,
                    game.universeClient.generateSettings.universeSettings.yDim - 1,
                    game.universeClient.generateSettings.universeSettings.zDim - 1
                ),
                game.universeClient.generateSettings.universeSettings.speedOfLight
            )

            game.universeClient.generateSettings.universeSettings.tDim = maxDelay + 1
            tDimLabel.setText(maxDelay + 1)
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

            // Change tDim
            val maxDelay: Int = Intervals.intDelay(
                Int3D(0, 0, 0),
                Int3D(
                    game.universeClient.generateSettings.universeSettings.xDim - 1,
                    game.universeClient.generateSettings.universeSettings.yDim - 1,
                    game.universeClient.generateSettings.universeSettings.zDim - 1
                ),
                game.universeClient.generateSettings.universeSettings.speedOfLight
            )

            game.universeClient.generateSettings.universeSettings.tDim = maxDelay + 1
            tDimLabel.setText(maxDelay + 1)
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

            // Change tDim
            val maxDelay: Int = Intervals.intDelay(
                Int3D(0, 0, 0),
                Int3D(
                    game.universeClient.generateSettings.universeSettings.xDim - 1,
                    game.universeClient.generateSettings.universeSettings.yDim - 1,
                    game.universeClient.generateSettings.universeSettings.zDim - 1
                ),
                game.universeClient.generateSettings.universeSettings.speedOfLight
            )

            game.universeClient.generateSettings.universeSettings.tDim = maxDelay + 1
            tDimLabel.setText(maxDelay + 1)
        }
        table.add(zDimSelectBox)

        table.row().space(10f)

        table.add(createLabel("Player after image duration: ", gdxSettings.normalFontSize))

        table.add(playerAfterImageLabel)

        table.row().space(10f)

        table.add(
            createLabel(
                "Player trajectory length (>= after image duration): ",
                gdxSettings.normalFontSize
            )
        )

        table.add(playerHistoricalInt4DLengthLabel)

        table.row().space(10f)

        table.add(createLabel("Group edge length", gdxSettings.normalFontSize))
        val groupEdgeLengthTextField = createTextField(
            game.universeClient.generateSettings.universeSettings.groupEdgeLength.toString(),
            gdxSettings.normalFontSize
        ) { groupEdgeLength, _ ->
            try {
                game.universeClient.generateSettings.universeSettings.groupEdgeLength =
                    groupEdgeLength.toDouble()
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