package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent

class GameScreenInfo(
    val game: RelativitizationGame,
    val worldMap: GameScreenWorldMap
) : ScreenComponent<ScrollPane>(game.assets) {
    private val gdxSetting = game.gdxSetting
    private val table: Table = Table()
    private val scrollPane: ScrollPane = createScrollPane(table)


    init {
        // Set background color
        table.background = assets.getBackgroundColor(0.2f, 0.3f, 0.5f, 1.0f)
        scrollPane.fadeScrollBars = false
        scrollPane.setFlickScroll(true)
    }

    override fun get(): ScrollPane {
        return scrollPane
    }

    override fun update() {
    }

    fun updateAll() {
        update()
        worldMap.update()
    }
}