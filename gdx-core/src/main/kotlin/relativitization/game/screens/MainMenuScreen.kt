package relativitization.game.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class MainMenuScreen(val game: RelativitizationGame) : TableScreen(game.assets) {

    val background: Image = assets.getImage("background/universe-background")
    val gdxSetting = game.gdxSetting


    override fun show() {
        // Add background before adding root table from super.show()
        stage.addActor(background)

        super.show()

        val newUniverseButton = createTextButton("New Universe", gdxSetting.buttonFontSize) {
            game.screen = NewUniverseScreen(game)
            dispose()
        }
        root.add(newUniverseButton).prefSize(500f, 100f).space(20f)
        root.row()
        val loadUniverseButton = createTextButton("Load Universe", gdxSetting.buttonFontSize)
        root.add(loadUniverseButton).prefSize(500f, 100f).space(20f)
        root.row()
        val joinUniverseButton = createTextButton("Join Universe", gdxSetting.buttonFontSize)
        root.add(joinUniverseButton).prefSize(500f, 100f).space(20f)
        root.row()
        val optionsButton = createTextButton("Options", gdxSetting.buttonFontSize)
        root.add(optionsButton).prefSize(500f, 100f).space(20f)
    }
}