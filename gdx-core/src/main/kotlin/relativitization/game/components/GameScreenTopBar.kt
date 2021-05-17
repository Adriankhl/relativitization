package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent

class GameScreenTopBar(val game: RelativitizationGame) : ScreenComponent<Table>(game.assets) {
    val table: Table = Table()
    override fun get(): Table {
        return table
    }
}