package relativitization.game.components.upper.defaults

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.components.upper.UpperInfoPane
import relativitization.universe.core.data.commands.Command

class CommandsInfoPane(val game: RelativitizationGame) : UpperInfoPane<ScrollPane>(game) {
    override val infoName: String = "Commands"

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
        if (game.universeClient.planDataAtPlayer.commandList != commandList) {
            updateCommandList()
            updateTable()
        }
        return scrollPane
    }

    override fun onCommandListChange() {
        updateCommandList()
        updateTable()
    }

    private fun updateCommandList() {
        commandList.clear()
        commandList.addAll(game.universeClient.planDataAtPlayer.commandList)
    }

    private fun updateTable() {
        table.clear()

        table.add(
            createLabel(
                "Command list: ",
                gdxSettings.bigFontSize
            )
        ).pad(20f)

        table.row().space(10f)

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

        val commandDescriptionLabel = createLabel(
            command.description(game.universeClient.universeClientSettings.playerId),
            gdxSettings.smallFontSize
        )
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