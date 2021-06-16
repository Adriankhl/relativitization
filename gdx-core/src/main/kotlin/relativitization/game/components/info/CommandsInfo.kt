package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent

class CommandsInfo(val game: RelativitizationGame) : ScreenComponent<Table>(game.assets){
    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    override fun getScreenComponent(): Table {
        return table
    }
}