package relativitization.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.Image
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class GameScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    private val background: Image = assets.getImage("background/universe-background")
    val gdxSetting = game.gdxSetting

    override fun show() {
        // wait first universe data before showing anything
        waitFirstData()

        // Add background before adding root table from super.show()
        stage.addActor(background)

        super.show()
    }

    private fun waitFirstData() {
        stage.clear()

        runBlocking {
            while(!game.universeClient.isCacheReady.isTrue()) {
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