package relativitization.game.screens

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Array
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class RegisterPlayerScreen(val game: RelativitizationGame) : TableScreen(game.assets)  {
    val gdxSetting = game.gdxSetting

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
        ) {
            game.universeClient.universeClientSettings.playerId = it
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

            playerIdSelectBox.items = Array(idList.toTypedArray<Int>())
        }
        root.add(updateButton).colspan(2)

        root.row().space(10f)

        root.add(createLabel("Pick your player id: ", gdxSetting.normalFontSize))
        root.add(playerIdSelectBox)

        root.row().space(10f)

        val registerStatusLabel = createLabel("", gdxSetting.normalFontSize)
        // Use late init to allow disabling the button in the function
        lateinit var registerPlayerButton: TextButton
        registerPlayerButton = createTextButton("Register", gdxSetting.normalFontSize) {
            runBlocking {
                val httpCode = game.universeClient.httpPostRegisterPlayer()
                if (httpCode == HttpStatusCode.OK) {
                    registerPlayerButton.isDisabled = true
                    registerPlayerButton.touchable = Touchable.disabled
                    registerStatusLabel.setText("Player id: ${game.universeClient.universeClientSettings.playerId}")
                } else {
                    registerStatusLabel.setText("Register player fail, http code: $httpCode")
                }
            }
        }

        root.add(createLabel("Register player id, can only register once: ", gdxSetting.normalFontSize))
        root.add(registerPlayerButton)

        root.row().space(10f)

        root.add(registerStatusLabel).colspan(2)

        root.row().space(10f)
    }
}