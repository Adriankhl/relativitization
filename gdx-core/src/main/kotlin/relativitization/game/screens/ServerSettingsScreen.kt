package relativitization.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class ServerSettingsScreen (val game: RelativitizationGame) : TableScreen(game.assets) {
    val gdxSetting = game.gdxSetting

    override fun show() {
        super.show()

        // do nothing now, til the end of the setting to submit this to the server
        root.add(createLabel("Admin password (for admin access to server): ", gdxSetting.normalFontSize))
        val adminPasswordTextField = createTextField("", gdxSetting.normalFontSize)
        root.add(adminPasswordTextField)

        root.row().space(10f)

        root.add(createLabel("Password (for holding player id): ", gdxSetting.normalFontSize))
        val passwordTextField = createTextField("", gdxSetting.normalFontSize) {
            game.universeClient.universeClientSettings.password = it
        }
        root.add(passwordTextField)

        root.row().space(10f)
    }
}