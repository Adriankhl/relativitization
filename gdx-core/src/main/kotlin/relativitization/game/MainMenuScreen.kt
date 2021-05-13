package relativitization.game

import com.badlogic.gdx.scenes.scene2d.ui.Image
import relativitization.client.UniverseClient
import relativitization.game.utils.Assets
import relativitization.game.utils.TableScreen

class MainMenuScreen(assets: Assets, gdxSetting: GdxSetting, universeClient: UniverseClient) : TableScreen(assets) {
    val background: Image = assets.getImage("background/universe-background")

    init {
        stage.addActor(background)
        root.add(textButton("New Universe", 1.5f)).width(300f).height(100f).space(20f)
        root.row()
        root.add(textButton("Load Universe", 1.5f)).width(300f).height(100f).space(20f)
        root.row()
        root.add(textButton("Join Universe", 1.5f)).width(300f).height(100f).space(20f)
        root.row()
        root.add(textButton("Options", 1.5f)).width(300f).height(100f).space(20f)
    }
}