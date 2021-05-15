package relativitization.game.screens

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Array
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class RegisterPlayerScreen(val game: RelativitizationGame) : TableScreen(game.assets)  {
    private val gdxSetting = game.gdxSetting

    override fun show() {
        super.show()

        var idList: List<Int> = listOf()

        root.add(createLabel("Type of available players: ", gdxSetting.normalFontSize))
        val getPlayerTypeSelectBox = createSelectBox(
            listOf("All", "Human only"),
            "All",
            gdxSetting.normalFontSize
        )
        root.add(getPlayerTypeSelectBox)

        root.row().space(10f)

        // Define before update button but show after update button
        val playerIdSelectBox = createSelectBox(
            idList,
            idList.getOrElse(0) { -1 },
            gdxSetting.normalFontSize
        ) { id, _ ->
            game.universeClient.universeClientSettings.playerId = id
        }

        val updateButton = createTextButton("Update") {
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

            playerIdSelectBox.items = Array(idList.toTypedArray())
        }
        root.add(updateButton).colspan(2)

        root.row().space(10f)

        root.add(createLabel("Pick your player id: ", gdxSetting.normalFontSize))
        root.add(playerIdSelectBox)

        root.row().space(10f)


        root.add(createLabel("Password (for holding your player id): ", gdxSetting.normalFontSize))
        val passwordTextField = createTextField(
            game.universeClient.universeClientSettings.password,
            gdxSetting.normalFontSize
        ) { password, _ ->
            game.universeClient.universeClientSettings.password = password
        }
        root.add(passwordTextField)

        root.row().space(10f)

        val registerStatusLabel = createLabel("", gdxSetting.normalFontSize)
        val registerPlayerButton: TextButton = createTextButton("Register", gdxSetting.normalFontSize) { button ->
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

        root.add(createLabel("Register player id, can only register once: ", gdxSetting.normalFontSize))
        root.add(registerPlayerButton)

        root.row().space(10f)

        root.add(registerStatusLabel).colspan(2)

        root.row().space(10f)


        val startStatusLabel = createLabel("", gdxSetting.normalFontSize)
        val startButton: TextButton = createTextButton("Start", gdxSetting.normalFontSize) {
            if (registerPlayerButton.touchable == Touchable.disabled) {
                runBlocking {
                    if (game.universeClient.getServerStatus().runningUniverse) {
                        game.screen = GameScreen(game)
                    } else {
                        val httpCode = game.universeClient.httpPostRunUniverse()
                        if (httpCode == HttpStatusCode.OK) {
                            game.screen = GameScreen(game)
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