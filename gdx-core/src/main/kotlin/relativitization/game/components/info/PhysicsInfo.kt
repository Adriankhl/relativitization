package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.components.GameScreenInfo
import relativitization.game.components.GameScreenWorldMap
import relativitization.game.utils.ScreenComponent

class PhysicsInfo(
    val game: RelativitizationGame,
) : ScreenComponent<ScrollPane>(game.assets) {

    private var table: Table = Table()
    private var scrollPane: ScrollPane = createScrollPane(table)

    init {
        table.background = assets.getBackgroundColor(0.2f, 0.3f, 0.5f, 1.0f)
    }

    override fun getActor(): ScrollPane {
        return scrollPane
    }
}