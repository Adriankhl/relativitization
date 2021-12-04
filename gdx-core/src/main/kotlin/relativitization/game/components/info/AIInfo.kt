package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent

class AIInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets)  {
    private val gdxSettings = game.gdxSettings

    private val table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    override fun getScreenComponent(): ScrollPane {
        return scrollPane
    }
}