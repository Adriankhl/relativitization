package relativitization.game.screens

import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class RegisterPlayerScreen(val game: RelativitizationGame) : TableScreen(game.assets)  {
    val gdxSetting = game.gdxSetting

    override fun show() {
        super.show()

        var idList: List<Int> = listOf()

        root.add(createLabel("Register Settings :", gdxSetting.hugeFontSIze))
    }
}