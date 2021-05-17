package relativitization.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Image
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import relativitization.game.RelativitizationGame
import relativitization.game.components.GameScreenTopBar
import relativitization.game.utils.TableScreen

class GameScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    private val background: Image = assets.getImage("background/universe-background")
    val gdxSetting = game.gdxSetting
    private val topBar: GameScreenTopBar = GameScreenTopBar(game)


    init {
        // request render when client is updated
        game.universeClient.updatableByClient.add { topBar.update() }
        game.universeClient.updatableByClient.add { Gdx.graphics.requestRendering() }
    }

    override fun show() {
        // wait first universe data before showing anything
        waitFirstData()

        // Add background before adding root table from super.show()
        stage.addActor(background)

        super.show()

        root.add(topBar.get())
    }

    override fun hide() {
        super.hide()
        game.universeClient.updatableByClient.clear()
    }

    override fun render(delta: Float) {
        super.render(delta)
    }

    override fun dispose() {
        super.dispose()
        game.universeClient.updatableByClient.clear()
    }

    private fun waitFirstData() {
        stage.clear()

        runBlocking {
            while (!game.universeClient.isCacheReady.isTrue()) {
                delay(200)
                logger.debug("Waiting universe data")
            }
        }
        game.universeClient.updateToLatestUniverseData3D()
    }


    companion object {
        private val logger = LogManager.getLogger()
    }
}