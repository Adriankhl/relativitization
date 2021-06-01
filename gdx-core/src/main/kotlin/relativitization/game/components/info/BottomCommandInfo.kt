package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent

class BottomCommandInfo(val game: RelativitizationGame) : ScreenComponent<Table>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private val commandNameLabel = createLabel("", gdxSettings.normalFontSize)
    private val commandDescriptionLabel = createLabel("", gdxSettings.smallFontSize)

    private val previousCommandButton = createImageButton(
        "basic/white-left-arrow",
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        0.7f,
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        gdxSettings.soundEffectsVolume
    ) {
        game.universeClient.previousCommand()
    }

    private val nextCommandButton = createImageButton(
        "basic/white-right-arrow",
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        0.7f,
        1.0f,
        1.0f,
        1.0f,
        1.0f,
        gdxSettings.soundEffectsVolume
    ) {
        game.universeClient.nextCommand()
    }

    private val confirmCommandTextButton = createTextButton(
        "Confirm",
        gdxSettings.normalFontSize,
        gdxSettings.soundEffectsVolume
    ) {
        game.universeClient.confirmCurrentCommand()
    }

    private val cancelCommandTextButton = createTextButton(
        "Cancel",
        gdxSettings.normalFontSize,
        gdxSettings.soundEffectsVolume
    ) {
        game.universeClient.cancelCurrentCommand()
    }

    init {
        table.background = assets.getBackgroundColor(0.2f, 0.2f, 0.2f, 1.0f)

        update()

        table.add(createHeaderTable())

        table.row().space(10f)

        table.add(createDescriptionScrollPane())

        table.row().space(10f)

        table.add(createButtonTable())
    }

    override fun getScreenComponent(): Table {
        return table
    }

    override fun onCurrentCommandChange() {
        update()
    }

    private fun createHeaderTable(): Table {
        val nestedTable: Table = Table()

        nestedTable.add(previousCommandButton).size(40f * gdxSettings.imageScale, 40f * gdxSettings.imageScale)

        nestedTable.add(commandNameLabel)

        nestedTable.add(nextCommandButton).size(40f * gdxSettings.imageScale, 40f * gdxSettings.imageScale)

        return nestedTable
    }

    private fun createDescriptionScrollPane(): ScrollPane {
        return ScrollPane(commandDescriptionLabel)
    }

    private fun createButtonTable(): Table {
        val nestedTable: Table = Table()

        nestedTable.add(confirmCommandTextButton).space(10f)

        nestedTable.add(cancelCommandTextButton)

        return nestedTable
    }

    private fun update() {
        commandNameLabel.setText(game.universeClient.currentCommand.name)
        commandDescriptionLabel.setText(game.universeClient.currentCommand.description())
        if (game.universeClient.isCurrentCommandStored()) {
            disableActor(confirmCommandTextButton)
            enableActor(cancelCommandTextButton)
        } else {
            enableActor(confirmCommandTextButton)
            disableActor(cancelCommandTextButton)
        }
    }
}