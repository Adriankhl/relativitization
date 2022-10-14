package relativitization.game.components.bottom

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.commands.CannotSendCommand
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.DummyCommand
import relativitization.universe.data.commands.ExecuteWarningCommand

class BottomCommandInfoPane(
    val game: RelativitizationGame,
) : ScreenComponent<ScrollPane>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()
    private val scrollPane: ScrollPane = createScrollPane(table)

    private val commandNameLabel = createLabel("", gdxSettings.normalFontSize)
    private val commandDescriptionLabel = createLabel("", gdxSettings.smallFontSize)

    private val previousCommandButton = createImageButton(
        name = "basic/white-left-arrow",
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
        aChecked = 0.5f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        it.isChecked = false
        game.universeClient.previousCommand()
    }

    private val nextCommandButton = createImageButton(
        name = "basic/white-right-arrow",
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
        aChecked = 0.5f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        it.isChecked = false
        game.universeClient.nextCommand()
    }

    private val confirmCommandTextButton = createTextButton(
        text = "Confirm",
        fontSize = gdxSettings.normalFontSize,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        game.universeClient.confirmCurrentCommand()
    }

    private val cancelCommandTextButton = createTextButton(
        text = "Cancel",
        fontSize = gdxSettings.normalFontSize,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
        game.universeClient.cancelCurrentCommand()
    }

    init {
        table.background = assets.getBackgroundColor(0.2f, 0.2f, 0.2f, 1.0f)

        scrollPane.fadeScrollBars = false
        scrollPane.setScrollingDisabled(true, false)
        scrollPane.setClamp(true)
        scrollPane.setOverscroll(false, false)

        update()

        table.add(createHeaderTable()).spaceTop(10f).growX()

        table.row().space(10f)

        table.add(createDescriptionScrollPane()).growY()

        table.row()

        table.add(createButtonTable()).pad(10f)
    }

    override fun getScreenComponent(): ScrollPane {
        return scrollPane
    }

    override fun onCurrentCommandChange() {
        update()
    }

    private fun createHeaderTable(): Table {
        val nestedTable: Table = Table()

        nestedTable.add(previousCommandButton)
            .size(40f * gdxSettings.imageScale, 40f * gdxSettings.imageScale)

        val nameLabelScrollPane = createScrollPane(commandNameLabel)

        nameLabelScrollPane.setScrollingDisabled(false, true)

        nestedTable.add(nameLabelScrollPane).expandX().growY()

        nestedTable.add(nextCommandButton)
            .size(40f * gdxSettings.imageScale, 40f * gdxSettings.imageScale)

        return nestedTable
    }

    private fun createDescriptionScrollPane(): ScrollPane {
        return createScrollPane(commandDescriptionLabel)
    }

    private fun createButtonTable(): Table {
        val nestedTable: Table = Table()

        nestedTable.add(confirmCommandTextButton).space(10f)

        nestedTable.add(cancelCommandTextButton)

        return nestedTable
    }

    private fun update() {
        val currentCommand: Command = game.universeClient.currentCommand
        if (currentCommand is AddEventCommand) {
            commandNameLabel.setText(translate(currentCommand.event.name()))
        } else {
            commandNameLabel.setText(translate(currentCommand.name()))
        }
        commandDescriptionLabel.setText(
            translate(
                currentCommand.description(
                    game.universeClient.universeClientSettings.playerId
                )
            )
        )

        if (game.universeClient.hasPreviousCommand()) {
            enableActor(previousCommandButton)
            previousCommandButton.isChecked = false
        } else {
            disableActor(previousCommandButton)
            previousCommandButton.isChecked = true
        }

        if (game.universeClient.hasNextCommand()) {
            enableActor(nextCommandButton)
            nextCommandButton.isChecked = false
        } else {
            disableActor(nextCommandButton)
            nextCommandButton.isChecked = true
        }

        if ((game.universeClient.currentCommand is CannotSendCommand) ||
            (game.universeClient.currentCommand is ExecuteWarningCommand) ||
            (game.universeClient.currentCommand is DummyCommand)
        ) {
            disableActor(confirmCommandTextButton)
            disableActor(cancelCommandTextButton)
        } else {
            if (game.universeClient.isCurrentCommandStored()) {
                disableActor(confirmCommandTextButton)
                enableActor(cancelCommandTextButton)
            } else {
                enableActor(confirmCommandTextButton)
                disableActor(cancelCommandTextButton)
            }
        }
    }
}