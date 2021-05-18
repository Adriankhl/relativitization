package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent

class GameScreenInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {
    private val gdxSetting = game.gdxSetting
    private val table: Table = Table()


    init {
        // Set background color
        table.background = assets.getBackGroundColor(0.2f, 0.3f, 0.5f, 1.0f)
    }

    override fun get(): ScrollPane {
        val scrollPane: ScrollPane = createScrollPane(table)
        scrollPane.fadeScrollBars = false
        scrollPane.setFlickScroll(false)
        return scrollPane
    }

    override fun update() {
    }
}