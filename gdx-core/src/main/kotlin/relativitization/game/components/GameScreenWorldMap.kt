package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent

class GameScreenWorldMap(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {
    private val gdxSetting = game.gdxSetting
    private val group: Group = Group()

    override fun get(): ScrollPane {
        val scrollPane: ScrollPane = createScrollPane(group)
        scrollPane.fadeScrollBars = false
        scrollPane.setFlickScroll(false)
        return scrollPane
    }

    override fun update() {
    }
}