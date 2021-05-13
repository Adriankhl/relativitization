package relativitization.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.Image
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class MainMenuScreen(val game: RelativitizationGame) : TableScreen(game.assets) {

    val assets = game.assets
    val background: Image = assets.getImage("background/universe-background")
    val gdxSetting = game.gdxSetting


    override fun show() {
        // Add background before adding root table from super.show()
        stage.addActor(background)

        super.show()

        val newUniverseButton = textButton("New Universe", gdxSetting.largeFontScale) {
            game.setScreen(NewUniverseScreen(game))
            dispose()
        }
        root.add(newUniverseButton).width(500f).height(100f).space(20f)
        root.row()
        val loadUniverseButton = textButton("Load Universe", gdxSetting.largeFontScale)
        root.add(loadUniverseButton).width(500f).height(100f).space(20f)
        root.row()
        val joinUniverseButton = textButton("Join Universe", gdxSetting.largeFontScale)
        root.add(joinUniverseButton).width(500f).height(100f).space(20f)
        root.row()
        val optionsButton = textButton("Options", gdxSetting.largeFontScale)
        root.add(optionsButton).width(500f).height(100f).space(20f)
    }
}