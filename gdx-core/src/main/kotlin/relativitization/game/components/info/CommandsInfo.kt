package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.commands.Command

class CommandsInfo(val game: RelativitizationGame) : ScreenComponent<Table>(game.assets){
    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private var commandList: List<Command> = listOf()

    init {
        table.background = assets.getBackgroundColor(0.2f, 0.2f, 0.2f, 1.0f)

        updateTable()
    }

    override fun getScreenComponent(): Table {
        return table
    }

    override fun onCommandListChange() {
        commandList = game.universeClient.commandList.getList()
        updateTable()
    }

    private fun updateTable() {
        table.clear()

        val headerLabel = createLabel("Command list: ", gdxSettings.bigFontSize)

        table.add(headerLabel)

        table.row().space(20f)

        for (command in commandList) {
            val commandTable = createCommandTable(command)
            table.add(commandTable).growX()

            table.row().space(20f)
        }
    }

    private fun createCommandTable(command: Command): Table {
        val nestedTable = Table()

        nestedTable.background = assets.getBackgroundColor(0.25f, 0.25f, 0.25f, 1.0f)

        val commandNameLabel = createLabel(command.name.toString(), gdxSettings.normalFontSize)

        val commandDescriptionLabel = createLabel(command.description, gdxSettings.smallFontSize)
        commandDescriptionLabel.wrap = true

        // button to select previous time
        val cancelButton: ImageButton = createImageButton(
            name = "basic/white-cross",
            rUp = 1.0f,
            gUp = 1.0f,
            bUp = 1.0f,
            aUp = 1.0f,
            rDown = 1.0f,
            gDown = 1.0f,
            bDown = 1.0f,
            aDown = 0.7f,
            rChecked = 1.0f,
            gChecked = 1.0f,
            bChecked = 1.0f,
            aChecked = 1.0f,
            soundVolume = gdxSettings.soundEffectsVolume
        ) {
            game.universeClient.commandList.remove(command)
        }

        nestedTable.add(commandNameLabel).growX()

        nestedTable.add(cancelButton).size(30f * gdxSettings.imageScale, 30f * gdxSettings.imageScale)

        nestedTable.row().space(10f)

        nestedTable.add(commandDescriptionLabel).colspan(2).growX().left()

        nestedTable.row().space(10f)

        return nestedTable
    }
}