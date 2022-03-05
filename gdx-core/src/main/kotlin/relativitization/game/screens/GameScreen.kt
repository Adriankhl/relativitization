package relativitization.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.components.GameScreenInfo
import relativitization.game.components.GameScreenTopBar
import relativitization.game.components.GameScreenWorldMap
import relativitization.game.utils.TableScreen
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.max

class GameScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    private val background: Image = assets.getImage("background/universe-background")
    val gdxSettings = game.gdxSettings

    private var originalZoom = 1.0f

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
    private val worldMapAndInfo = createSplitPane(
        worldMap.getScreenComponent(),
        info.getScreenComponent(),
        false
    )
    private val topBar: GameScreenTopBar = GameScreenTopBar(game, info)

    // Button to trigger gdx settings change
    private val playerDeadBackground = createImage(
        name = "basic/white-pixel",
        xPos = 0f,
        yPos = 0f,
        width = Gdx.graphics.width.toFloat(),
        height = Gdx.graphics.height.toFloat(),
        r = 0.0f,
        g = 0.0f,
        b = 0.0f,
        a = 0.6f,
        soundVolume = gdxSettings.soundEffectsVolume
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
        resizeBackgroundImage()
        stage.addActor(background)

        super.show()

        // Adjust split pane position from gdx setting
        worldMapAndInfo.splitAmount = gdxSettings.worldMapAndInfoSplitAmount

        // Fix minimal top Bar height to preferred height
        root.add(topBar.getScreenComponent()).growX().top()
            .minHeight(topBar.getScreenComponent().prefHeight)
            .maxHeight(topBar.getScreenComponent().prefHeight)

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
                        game.universeClient.clearSelected()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                // Store original value for zooming by gesture
                originalZoom = gdxSettings.mapZoomRelativeToFullMap
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                if (originalZoom != gdxSettings.mapZoomRelativeToFullMap) {
                    game.changeGdxSettings()
                }
            }
        })

        stage.addListener(object : ActorGestureListener() {
            override fun zoom(event: InputEvent?, initialDistance: Float, distance: Float) {
                gdxSettings.mapZoomRelativeToFullMap = (distance / initialDistance) * originalZoom
            }
        })
    }

    override fun render(delta: Float) {
        super.render(delta)
        topBar.render()

        if (game.universeClient.isPlayerDead) {
            playerDeadButton.setPosition(
                Gdx.graphics.width / 2.0f - playerDeadButton.width / 2,
                Gdx.graphics.height / 2.0f - playerDeadButton.height / 2
            )
            stage.addActor(playerDeadBackground)
            stage.addActor(playerDeadButton)
        }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        resizeBackgroundImage()
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
            // Only update the stored split amount of the split screen is not too close to the edge
            if (worldMapAndInfo.splitAmount < worldMapAndInfo.maxSplitAmount * 0.9) {
                gdxSettings.worldMapAndInfoSplitAmount = worldMapAndInfo.splitAmount
            }
            worldMapAndInfo.splitAmount = worldMapAndInfo.maxSplitAmount
        }
    }

    override fun onIsPlayerDeadChange() {
        Gdx.graphics.requestRendering()
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

    private fun resizeBackgroundImage() {
        val screenWidth: Float = Gdx.graphics.width.toFloat()
        val screenHeight: Float = Gdx.graphics.height.toFloat()
        val backgroundImageWidth: Float = background.drawable.minWidth
        val backgroundImageHeight: Float = background.drawable.minHeight
        background.setScale(
            max(
                screenWidth / backgroundImageWidth,
                screenHeight / backgroundImageHeight
            )
        )
    }

    private fun syncComponentSetting() {
        info.reRegisterUpperInfoComponent()
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}