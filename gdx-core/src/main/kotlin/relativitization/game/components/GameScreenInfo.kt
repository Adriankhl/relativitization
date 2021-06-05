package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import relativitization.game.RelativitizationGame
import relativitization.game.ShowingInfoType
import relativitization.game.components.info.BottomCommandInfo
import relativitization.game.components.info.OverviewInfo
import relativitization.game.components.info.PhysicsInfo
import relativitization.game.utils.ScreenComponent

class GameScreenInfo(val game: RelativitizationGame) : ScreenComponent<SplitPane>(game.assets) {
    private val gdxSettings = game.gdxSettings

    private val overviewInfo: OverviewInfo = OverviewInfo(game)
    private val physicsInfo: PhysicsInfo = PhysicsInfo(game)

    private val upperInfoScrollPane: ScrollPane = createScrollPane(overviewInfo.getScreenComponent())
    private val bottomCommandInfo: BottomCommandInfo = BottomCommandInfo(game)

    private val infoAndCommand = createSplitPane(upperInfoScrollPane, bottomCommandInfo.getScreenComponent(), true)


    init {
        // Add child screen component
        addChildScreenComponent(bottomCommandInfo)
        addChildScreenComponent(overviewInfo)
        addChildScreenComponent(physicsInfo)

        // Set background color
        upperInfoScrollPane.fadeScrollBars = false
        upperInfoScrollPane.setFlickScroll(true)

        infoAndCommand.splitAmount = gdxSettings.upperInfoAndBottomCommandSplitAmount
    }

    override fun getScreenComponent(): SplitPane {
        return infoAndCommand
    }

    override fun onGdxSettingsChange() {
        // Show info type based on setting
        when (gdxSettings.showingInfoType) {
            ShowingInfoType.OVERVIEW -> upperInfoScrollPane.actor = overviewInfo.getScreenComponent()
            ShowingInfoType.PHYSICS -> upperInfoScrollPane.actor = physicsInfo.getScreenComponent()
        }

        // Show bottom command or not
        if (gdxSettings.showingBottomCommand) {
            infoAndCommand.isVisible = true
        } else {
            gdxSettings.upperInfoAndBottomCommandSplitAmount = infoAndCommand.splitAmount
            infoAndCommand.isVisible = false
        }
    }
}