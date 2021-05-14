package relativitization.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class ServerSettingsScreen (val game: RelativitizationGame) : TableScreen(game.assets) {
    val gdxSetting = game.gdxSetting

    override fun show() {
        super.show()

        // Wait at the end of server
        val adminPasswordTextField = createTextField("", gdxSetting.normalFontSize)
        root.add(adminPasswordTextField)

        root.row().space(10f)
    }
}