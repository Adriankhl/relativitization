package relativitization.game.screens

import com.badlogic.gdx.utils.Array
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
        )

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
    }
}