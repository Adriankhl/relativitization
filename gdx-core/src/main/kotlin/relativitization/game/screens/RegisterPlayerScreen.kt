package relativitization.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Array
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen
import relativitization.universe.UniverseClientSettings

class RegisterPlayerScreen(val game: RelativitizationGame) : TableScreen(game.assets)  {
    private val gdxSettings = game.gdxSettings

    private val registerPlayerButton: TextButton = createTextButton("Register", gdxSettings.normalFontSize, gdxSettings.soundEffectsVolume) { button ->
        if (game.universeClient.universeClientSettings.playerId >= 0) {
            runBlocking {
                val httpCode = game.universeClient.httpPostRegisterPlayer()
                if (httpCode == HttpStatusCode.OK) {
                    disableActor(button)
                    registerStatusLabel.setText("Player id: ${game.universeClient.universeClientSettings.playerId}")
                } else {
                    registerStatusLabel.setText("Register player fail, http code: $httpCode")
                }
            }
        } else {
            registerStatusLabel.setText("Player id smaller than 0, please pick a valid id")
        }
    }

    private val registerStatusLabel = createLabel("", gdxSettings.normalFontSize)

    override fun show() {
        super.show()

        root.add(createRegisterPlayerScrollPane()).pad(20f).growX()

        root.row().space(10f)

        root.add(createButtonTable())
    }

    /**
     * Create table for start button and cancel button
     */
    private fun createButtonTable(): Table {
        val nestedTable = Table()

        val startStatusLabel = createLabel("", gdxSettings.normalFontSize)
        val startButton: TextButton = createTextButton("Start", gdxSettings.normalFontSize, gdxSettings.soundEffectsVolume) {
            if (registerPlayerButton.touchable == Touchable.disabled) {
                runBlocking {
                    if (game.universeClient.getCurrentServerStatus().isUniverseRunning) {
                        // Not showing because it is too fast?
                        startStatusLabel.setText("Universe already running, waiting universe data")
                        game.screen = GameScreen(game)
                        dispose()
                    } else {
                        val httpCode = game.universeClient.httpPostRunUniverse()
                        if (httpCode == HttpStatusCode.OK) {
                            // Not showing because it is too fast?
                            startStatusLabel.setText("Run universe success, waiting universe data")
                            game.screen = GameScreen(game)
                            dispose()
                        } else {
                            startStatusLabel.setText("Can't start universe")
                        }
                    }
                }
            } else {
                startStatusLabel.setText("Please register a player id")
            }
        }

        val cancelButton = createTextButton(
            "Cancel",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = MainMenuScreen(game)
        }

        nestedTable.add(startButton).space(10f)
        nestedTable.add(cancelButton).space(10f)
        nestedTable.row().space(10f)
        nestedTable.add(startStatusLabel).colspan(2)

        return nestedTable
    }


    private fun createRegisterPlayerScrollPane(): ScrollPane {
        val table = Table()

        table.add(createLabel("Register player Settings :", gdxSettings.hugeFontSize)).colspan(2)

        table.row().space(20f)

        var idList: List<Int> = listOf()

        table.add(createLabel("Type of available players: ", gdxSettings.normalFontSize))
        val getPlayerTypeSelectBox = createSelectBox(
            listOf("All", "Human only"),
            "All",
            gdxSettings.normalFontSize
        )
        table.add(getPlayerTypeSelectBox)

        table.row().space(10f)

        // Define before update button but show after update button
        val playerIdSelectBox = createSelectBox(
            idList,
            idList.getOrElse(0) { -1 },
            gdxSettings.normalFontSize
        ) { id, _ ->
            val newUniverseClientSettings: UniverseClientSettings = game.universeClient.universeClientSettings.copy(
                playerId = id
            )
            runBlocking {
                game.universeClient.setUniverseClientSettings(newUniverseClientSettings)
            }
        }

        val updateButton = createTextButton("Update", gdxSettings.normalFontSize, gdxSettings.soundEffectsVolume) {
            when (getPlayerTypeSelectBox.selected) {
                "All" -> {
                    runBlocking {
                        idList = game.universeClient.httpGetAvailableIdList()
                    }
                }
                "Human only" -> {
                    runBlocking {
                        idList = game.universeClient.httpGetAvailableHumanIdList()
                    }
                }
                else -> {
                    runBlocking {
                        idList = game.universeClient.httpGetAvailableIdList()
                    }
                }
            }

            // Prevent null pointer exception at playerIdSelectBox
            if (idList.isEmpty()) {
                idList = listOf(-1)
            }

            playerIdSelectBox.items = Array(idList.sorted().toTypedArray())
        }
        table.add(updateButton).colspan(2)

        table.row().space(10f)

        table.add(createLabel("Pick your player id: ", gdxSettings.normalFontSize))
        table.add(playerIdSelectBox)

        table.row().space(10f)


        table.add(createLabel("Password (for holding your player id): ", gdxSettings.normalFontSize))
        val passwordTextField = createTextField(
            game.universeClient.universeClientSettings.password,
            gdxSettings.normalFontSize
        ) { password, _ ->
            val newUniverseClientSettings: UniverseClientSettings = game.universeClient.universeClientSettings.copy(
                password = password
            )
            runBlocking {
                game.universeClient.setUniverseClientSettings(newUniverseClientSettings)
            }
        }
        table.add(passwordTextField)

        table.row().space(10f)


        table.add(createLabel("Register player id, can only register once: ", gdxSettings.normalFontSize))
        table.add(registerPlayerButton)

        table.row().space(10f)

        table.add(registerStatusLabel).colspan(2)

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
}