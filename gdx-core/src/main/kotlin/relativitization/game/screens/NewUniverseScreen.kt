package relativitization.game.screens

import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class NewUniverseScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    val gdxSetting = game.gdxSetting

    override fun show() {
        super.show()

        addXDimSelectBox()
    }

    private fun addXDimSelectBox() {
        root.add(createLabel("Universe x dimension: ", gdxSetting.normalFontSize))
    }
}