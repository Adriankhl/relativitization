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
import relativitization.game.components.ShowingInfo
import relativitization.game.utils.ScreenComponent
import relativitization.game.utils.TableScreen

class GameScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    private val background: Image = assets.getImage("background/universe-background")
    val gdxSetting = game.gdxSetting

    init {
        // stage.clear()
        game.clearOnChangeFunctionList()
        if (!game.isGameStarted) {
            // wait first universe data before showing anything
            waitFirstData()
            game.isGameStarted = true
        }
    }

    private val worldMap: GameScreenWorldMap = GameScreenWorldMap(game)
    private val info: GameScreenInfo = GameScreenInfo(game, worldMap)
    private val worldMapAndInfo = createSplitPane(worldMap.getActor(), info.getActor(), false)
    private val topBar: GameScreenTopBar = GameScreenTopBar(game, worldMap, info, worldMapAndInfo)

    init {
        addChildScreenComponent(worldMap)
        addChildScreenComponent(info)
        addChildScreenComponent(topBar)

        addAllComponentToClient(game, this)
    }


    override fun show() {
        // Add background before adding root table from super.show()
        stage.addActor(background)

        super.show()

        // Adjust split pane position from gdx setting
        worldMapAndInfo.splitAmount = gdxSetting.worldMapAndInfoSplitAmount

        // Add all screen component to universe client

        // Fix minimal top Bar height to preferred height
        root.add(topBar.getActor()).growX().top().minHeight(topBar.getActor().prefHeight)

        root.row()

        root.add(worldMapAndInfo).growX().growY()

        stage.addListener(object : InputListener() {
            override fun keyTyped(event: InputEvent?, character: Char): Boolean {
                logger.debug("Key typed: $character")
                return when (character) {
                    '+' -> {
                        gdxSetting.mapZoomRelativeToFullMap *= gdxSetting.mapZoomFactor
                        game.changeGdxSetting()
                        true
                    }
                    '-' -> {
                        gdxSetting.mapZoomRelativeToFullMap *= gdxSetting.mapZoomFactor
                        game.changeGdxSetting()
                        true
                    }
                    '0' -> {
                        gdxSetting.mapZoomRelativeToFullMap = 1.0f
                        game.changeGdxSetting()
                        true
                    }
                    '<' -> {
                        game.universeClient.previousUniverseData3D()
                        true
                    }
                    '>' -> {
                        game.universeClient.nextUniverseData3D()
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
                        gdxSetting.showingInfo = false
                        game.changeGdxSetting()
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
        game.clearOnChangeFunctionList()
    }

    override fun dispose() {
        super.dispose()
        game.clearOnChangeFunctionList()
    }

    override fun onGdxSettingsChange() {
        if (gdxSetting.showingInfo) {
            worldMapAndInfo.splitAmount = gdxSetting.worldMapAndInfoSplitAmount
        } else {
            worldMapAndInfo.splitAmount = 1.0f
        }
    }

    private fun waitFirstData() {
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