package relativitization.game.screens

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Array
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen
import relativitization.universe.UniverseClientSettings

class RegisterPlayerScreen(val game: RelativitizationGame) : TableScreen(game.assets)  {
    private val gdxSettings = game.gdxSettings

    override fun show() {
        super.show()

        var idList: List<Int> = listOf()

        root.add(createLabel("Type of available players: ", gdxSettings.normalFontSize))
        val getPlayerTypeSelectBox = createSelectBox(
            listOf("All", "Human only"),
            "All",
            gdxSettings.normalFontSize
        )
        root.add(getPlayerTypeSelectBox)

        root.row().space(10f)

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

            playerIdSelectBox.items = Array(idList.sorted().toTypedArray())
        }
        root.add(updateButton).colspan(2)

        root.row().space(10f)

        root.add(createLabel("Pick your player id: ", gdxSettings.normalFontSize))
        root.add(playerIdSelectBox)

        root.row().space(10f)


        root.add(createLabel("Password (for holding your player id): ", gdxSettings.normalFontSize))
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
        root.add(passwordTextField)

        root.row().space(10f)

        val registerStatusLabel = createLabel("", gdxSettings.normalFontSize)
        val registerPlayerButton: TextButton = createTextButton("Register", gdxSettings.normalFontSize, gdxSettings.soundEffectsVolume) { button ->
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

        root.add(createLabel("Register player id, can only register once: ", gdxSettings.normalFontSize))
        root.add(registerPlayerButton)

        root.row().space(10f)

        root.add(registerStatusLabel).colspan(2)

        root.row().space(10f)


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

        root.add(startButton).colspan(2)

        root.row().space(10f)

        root.add(startStatusLabel).colspan(2)
    }
}