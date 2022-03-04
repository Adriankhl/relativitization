package relativitization.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Image
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen
import kotlin.math.max

class MainMenuScreen(val game: RelativitizationGame) : TableScreen(game.assets) {

    private val background: Image = assets.getImage("background/universe-background")
    private val gdxSettings = game.gdxSettings


    override fun show() {
        // Add background before adding root table from super.show()
        resizeBackgroundImage()
        stage.addActor(background)

        super.show()

        val newUniverseButton = createTextButton(
            "New Universe",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = NewUniverseScreen(game)
            dispose()
        }
        root.add(newUniverseButton).prefSize(500f, 100f)

        root.row().space(20f)

        val loadUniverseButton = createTextButton(
            "Load Universe",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = LoadUniverseScreen(game)
            dispose()
        }
        root.add(loadUniverseButton).prefSize(500f, 100f)

        root.row().space(20f)

        val joinUniverseButton = createTextButton(
            "Join Universe",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = JoinUniverseScreen(game)
            dispose()
        }
        root.add(joinUniverseButton).prefSize(500f, 100f)

        root.row().space(20f)

        val gdxSettingsButton = createTextButton(
            "Client Settings",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = ClientSettingsScreen(game, false)
            dispose()
        }
        root.add(gdxSettingsButton).prefSize(500f, 100f)

        root.row().space(20f)

        val helpButton = createTextButton(
            "Help",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = HelpScreen(game, false)
            dispose()
        }
        root.add(helpButton).prefSize(500f, 100f)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        resizeBackgroundImage()
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
}