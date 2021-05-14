package relativitization.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class ServerSettingsScreen (val game: RelativitizationGame) : TableScreen(game.assets) {
    val gdxSetting = game.gdxSetting

    override fun show() {
        super.show()
    }

    private fun addAdminPasswordTextField(table: Table) {

    }
}