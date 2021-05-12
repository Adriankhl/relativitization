package relativitization.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import relativitization.game.utils.Assets
import relativitization.game.utils.BaseScreen

class MainMenuScreen(assets: Assets) : BaseScreen(assets) {
    val background: Image = assets.getImage("background/universe-background")

    init {
        stage.addActor(background)
    }
}