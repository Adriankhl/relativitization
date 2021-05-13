package relativitization.game

import com.badlogic.gdx.scenes.scene2d.ui.Image
import relativitization.client.UniverseClient
import relativitization.game.utils.Assets
import relativitization.game.utils.TableScreen

class MainMenuScreen(
    game: RelativitizationGame,
) : TableScreen(game.assets) {

    val assets = game.assets
    val background: Image = assets.getImage("background/universe-background")
    val gdxSetting = game.gdxSetting

    init {
        stage.addActor(background)
        val newUniverseButton = textButton("New Universe", gdxSetting.largeFontScale)
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