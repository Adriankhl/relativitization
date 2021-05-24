package relativitization.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import relativitization.game.RelativitizationGame
import relativitization.game.components.GameScreenInfo
import relativitization.game.components.GameScreenTopBar
import relativitization.game.components.GameScreenWorldMap
import relativitization.game.utils.TableScreen

class GameScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    private val background: Image = assets.getImage("background/universe-background")
    val gdxSetting = game.gdxSetting

    init {
        // wait first universe data before showing anything
        waitFirstData()
    }

    private val topBar: GameScreenTopBar = GameScreenTopBar(game)
    private val worldMap: GameScreenWorldMap = GameScreenWorldMap(game)
    private val info: GameScreenInfo = GameScreenInfo(game)
    private val worldMapAndInfo = createSplitPane(worldMap.get(), info.get(), false)


    init {
        // request render when client is updated
        game.universeClient.updatableByClient.add { topBar.update() }
        game.universeClient.updatableByClient.add { Gdx.graphics.requestRendering() }
    }

    override fun show() {
        // Add background before adding root table from super.show()
        stage.addActor(background)

        super.show()

        root.add(topBar.get()).growX().top()

        root.row()

        root.add(worldMapAndInfo).growX().growY()

        stage.addListener(object : InputListener() {
            override fun keyTyped(event: InputEvent?, character: Char): Boolean {
                logger.debug("Key typed: $character")
                return when (character) {
                    '+' -> {
                        worldMap.zoomIn()
                        true
                    }
                    '-' -> {
                        worldMap.zoomOut()
                        true
                    }
                    '0' -> {
                        worldMap.zoomToFullMap()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

            override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
                logger.debug("Key down code: $keycode")
                return when (keycode) {
                    Input.Keys.ESCAPE -> {
                        if (worldMapAndInfo.splitAmount < worldMapAndInfo.maxSplitAmount) {
                            worldMapAndInfo.splitAmount = worldMapAndInfo.maxSplitAmount
                        }
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        })
    }

    override fun hide() {
        super.hide()
        game.universeClient.updatableByClient.clear()
    }

    override fun dispose() {
        super.dispose()
        game.universeClient.updatableByClient.clear()
    }

    private fun waitFirstData() {
        stage.clear()

        runBlocking {
            while (!game.universeClient.isNewDataReady.isTrue()) {
                delay(200)
                logger.debug("Waiting universe data")
            }
            game.universeClient.pickLatestUniverseData3D()
        }
    }


    companion object {
        private val logger = LogManager.getLogger()
    }
}