package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent

class BottomCommandInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()
    private var scrollPane: ScrollPane = createScrollPane(table)

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
    }

    override fun getScreenComponent(): ScrollPane {
        return scrollPane
    }
}