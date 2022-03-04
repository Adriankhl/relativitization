package relativitization.game.screens

import com.badlogic.gdx.Gdx
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class WaitingGameScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    val gdxSettings = game.gdxSettings

    override fun show() {
        super.show()
        Gdx.graphics.isContinuousRendering = true
        root.add(
            createLabel(
                "Waiting...",
                gdxSettings.bigFontSize
            )
        )
    }

    override fun render(delta: Float) {
        super.render(delta)
        runBlocking {
            if (game.universeClient.isNewDataReady.isTrue()) {
                Gdx.graphics.isContinuousRendering = gdxSettings.isContinuousRendering
                game.screen = GameScreen(game)
            }
        }
    }
}