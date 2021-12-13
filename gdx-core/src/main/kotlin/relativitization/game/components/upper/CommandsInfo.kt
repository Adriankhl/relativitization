package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.name

class CommandsInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {
    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private val commandList: MutableList<Command> = mutableListOf()

    init {
        table.background = assets.getBackgroundColor(0.2f, 0.2f, 0.2f, 1.0f)

        // Configure scroll pane
        scrollPane.fadeScrollBars = false
        scrollPane.setClamp(true)
        scrollPane.setOverscroll(false, false)

        updateTable()
    }

    override fun getScreenComponent(): ScrollPane {
        return scrollPane
    }

    override fun onCommandListChange() {
        commandList.clear()
        commandList.addAll(game.universeClient.planDataAtPlayer.commandList)
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

            table.row().space(30f)
        }
    }

    private fun createCommandTable(command: Command): Table {
        val nestedTable = Table()

        nestedTable.background = assets.getBackgroundColor(0.25f, 0.25f, 0.25f, 1.0f)

        val commandNameLabel = createLabel(command.name(), gdxSettings.normalFontSize)

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
            game.universeClient.planDataAtPlayer.removeCommand(command)
        }

        nestedTable.add(commandNameLabel).growX()

        nestedTable.add(cancelButton)
            .size(30f * gdxSettings.imageScale, 30f * gdxSettings.imageScale)

        nestedTable.row().space(10f)

        nestedTable.add(commandDescriptionLabel).colspan(2).growX().left()

        return nestedTable
    }
}