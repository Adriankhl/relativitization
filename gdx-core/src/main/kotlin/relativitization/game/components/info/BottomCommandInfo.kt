package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import relativitization.game.RelativitizationGame
import relativitization.game.components.GameScreenWorldMap
import relativitization.game.utils.ScreenComponent

class BottomCommandInfo(
    val game: RelativitizationGame,
    val worldMap: GameScreenWorldMap
) : ScreenComponent<ScrollPane>(game.assets) {


    override fun get(): ScrollPane {
        TODO("Not yet implemented")
    }

    override fun update() {
        TODO("Not yet implemented")
    }
}