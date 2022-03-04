package relativitization.game.screens

import com.badlogic.gdx.Gdx
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen
import relativitization.universe.maths.number.Notation

class LoadingGameScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    val gdxSettings = game.gdxSettings

    var loadingTime: Double = 0.0

    private val loadingLabel = createLabel(
        "Loading...",
        gdxSettings.bigFontSize
    )

    override fun show() {
        super.show()
        Gdx.graphics.isContinuousRendering = true
        root.add(
            loadingLabel
        )
    }

    override fun render(delta: Float) {
        super.render(delta)
        runBlocking {
            if (game.universeClient.isNewDataReady.isTrue()) {
                Gdx.graphics.isContinuousRendering = gdxSettings.isContinuousRendering
                game.screen = GameScreen(game)
            } else {
                loadingTime += delta
                val displayTime: Int = loadingTime.toInt()
                loadingLabel.setText("Loading... $displayTime")
            }
        }
    }
}