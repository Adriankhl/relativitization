package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.commands.Command

class CommandsInfo(val game: RelativitizationGame) : ScreenComponent<Table>(game.assets){
    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private var commandList: List<Command> = listOf()

    override fun getScreenComponent(): Table {
        return table
    }

    override fun onCommandListChange() {
        commandList = game.universeClient.commandList.getList()
    }

    fun createCommandTable(command: Command): Table {
        val nestedTable = Table()

        return nestedTable
    }
}