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

    private val messageLabel = createLabel(
        "Scroll the top bar to the right and click \"?\" to see the tutorial",
        gdxSettings.normalFontSize,
    )

    override fun show() {
        super.show()
        Gdx.graphics.isContinuousRendering = true

        root.add(loadingLabel)

        root.row().space(30f)

        root.add(messageLabel)
    }

    override fun render(delta: Float) {
        super.render(delta)
        runBlocking {
            if (game.universeClient.isNewDataReady.isTrue() && (loadingTime > 5.0)) {
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