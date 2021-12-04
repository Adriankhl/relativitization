package relativitization.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Image
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class DeadScreen(val game: RelativitizationGame) : TableScreen(game.assets) {

    val gdxSettings = game.gdxSettings

    override fun show() {
        super.show()

        val deadButton = createTextButton(
            "You are dead, quit game",
            gdxSettings.normalFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            runBlocking {
                game.universeClient.addToOnServerStatusChangeFunctionList { Gdx.app.exit() }
            }
        }

        root.add(deadButton)
    }
}