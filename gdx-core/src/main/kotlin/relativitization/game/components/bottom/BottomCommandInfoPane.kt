package relativitization.game.components.bottom

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.commands.CannotSendCommand
import relativitization.universe.data.commands.ExecuteWarningCommand
import relativitization.universe.data.commands.name

class BottomCommandInfoPane(
    val game: RelativitizationGame,
) : ScreenComponent<ScrollPane>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()
    private val scrollPane: ScrollPane = createScrollPane(table)

    private val commandNameLabel = createLabel("", gdxSettings.normalFontSize)
    private val commandDescriptionLabel = createLabel("", gdxSettings.smallFontSize)

    private val commandTimeLabel = createLabel("", gdxSettings.smallFontSize)

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
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
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
        aChecked = 1.0f,
        soundVolume = gdxSettings.soundEffectsVolume
    ) {
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
        table.row()
        table.add(commandTimeLabel)

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
        commandNameLabel.setText(translate(game.universeClient.currentCommand.name()))
        commandDescriptionLabel.setText(translate(game.universeClient.currentCommand.description()))
        commandTimeLabel.setText(translate("Time: ") + "${game.universeClient.currentCommand.fromInt4D.t}")

        if (game.universeClient.currentCommand is CannotSendCommand ||
            game.universeClient.currentCommand is ExecuteWarningCommand
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