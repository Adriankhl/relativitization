package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.components.GameScreenInfo
import relativitization.game.components.GameScreenWorldMap
import relativitization.game.utils.ScreenComponent

class OverviewInfo(
    val game: RelativitizationGame,
    val worldMap: GameScreenWorldMap,
    val info: GameScreenInfo,
) : ScreenComponent<ScrollPane>(game.assets) {

    private var scrollPane: ScrollPane = createScrollPane(Table())

    override fun get(): ScrollPane {
        return scrollPane
    }

    override fun update() {
        TODO("Not yet implemented")
    }

    fun updateScrollPane() {

    }
}