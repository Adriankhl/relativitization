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
    val gdxSettings = game.gdxSettings

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
    private val info: GameScreenInfo = GameScreenInfo(game)
    private val worldMapAndInfo = createSplitPane(worldMap.getScreenComponent(), info.getScreenComponent(), false)
    private val topBar: GameScreenTopBar = GameScreenTopBar(game)

    // Button to trigger gdx settings change
    private val helloUniverseButtonBackground = createImage(
        "basic/white-pixel",
        0f,
        0f,
        Gdx.graphics.width.toFloat(),
        Gdx.graphics.height.toFloat(),
        0.0f,
        0.0f,
        0.0f,
        0.6f,
        gdxSettings.soundEffectsVolume
    )

    private val helloUniverseButton = createTextButton(
        "Hello Universe!",
        gdxSettings.maxFontSize,
        gdxSettings.soundEffectsVolume
    ) {
        it.remove()
        helloUniverseButtonBackground.remove()
        game.changeGdxSettings()
    }

    // Button to trigger gdx settings change
    private val playerDeadBackground = createImage(
        "basic/white-pixel",
        0f,
        0f,
        Gdx.graphics.width.toFloat(),
        Gdx.graphics.height.toFloat(),
        0.0f,
        0.0f,
        0.0f,
        0.6f,
        gdxSettings.soundEffectsVolume
    )

    private val playerDeadButton = createTextButton(
        "You are dead!",
        gdxSettings.maxFontSize,
        gdxSettings.soundEffectsVolume
    ) {
        dispose()
        game.screen = DeadScreen(game)
    }

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
        worldMapAndInfo.splitAmount = gdxSettings.worldMapAndInfoSplitAmount

        // Fix minimal top Bar height to preferred height
        root.add(topBar.getScreenComponent()).growX().top().minHeight(topBar.getScreenComponent().prefHeight)

        root.row()

        root.add(worldMapAndInfo).growX().growY()

        stage.addListener(object : InputListener() {
            override fun keyTyped(event: InputEvent?, character: Char): Boolean {
                logger.debug("Key typed: $character")
                return when (character) {
                    '+' -> {
                        gdxSettings.mapZoomRelativeToFullMap *= gdxSettings.mapZoomFactor
                        game.changeGdxSettings()
                        true
                    }
                    '-' -> {
                        gdxSettings.mapZoomRelativeToFullMap /= gdxSettings.mapZoomFactor
                        game.changeGdxSettings()
                        true
                    }
                    ')' -> {
                        gdxSettings.mapZoomRelativeToFullMap = 1.0f
                        game.changeGdxSettings()
                        true
                    }
                    '<' -> {
                        runBlocking {
                            game.universeClient.previousUniverseData3D()
                        }
                        true
                    }
                    '>' -> {
                        runBlocking {
                            game.universeClient.nextUniverseData3D()
                        }
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
                        gdxSettings.showingInfo = false
                        game.changeGdxSettings()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        })


        helloUniverseButton.setPosition(Gdx.graphics.width / 2.0f - helloUniverseButton.width / 2, Gdx.graphics.height / 2.0f - helloUniverseButton.height / 2)
        stage.addActor(helloUniverseButtonBackground)
        stage.addActor(helloUniverseButton)
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
        if (gdxSettings.showingInfo) {
            worldMapAndInfo.splitAmount = gdxSettings.worldMapAndInfoSplitAmount
        } else {
            gdxSettings.worldMapAndInfoSplitAmount = worldMapAndInfo.splitAmount
            worldMapAndInfo.splitAmount = worldMapAndInfo.maxSplitAmount
        }
    }

    override fun onIsPlayerDeadChange() {
        if (game.universeClient.isPlayerDead) {
            playerDeadButton.setPosition(Gdx.graphics.width / 2.0f - playerDeadButton.width / 2, Gdx.graphics.height / 2.0f - playerDeadButton.height / 2)
            stage.addActor(playerDeadBackground)
            stage.addActor(playerDeadButton)
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