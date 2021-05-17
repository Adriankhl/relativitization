package relativitization.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.Image
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class GameScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    private val background: Image = assets.getImage("background/universe-background")
    val gdxSetting = game.gdxSetting

    override fun show() {
        // Add background before adding root table from super.show()
        stage.addActor(background)

        super.show()
    }

    private fun wait
}