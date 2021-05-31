package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.ShowingInfoType
import relativitization.game.components.info.BottomCommandInfo
import relativitization.game.components.info.OverviewInfo
import relativitization.game.components.info.PhysicsInfo
import relativitization.game.utils.ScreenComponent

class GameScreenInfo(val game: RelativitizationGame) : ScreenComponent<SplitPane>(game.assets) {
    private val gdxSettings = game.gdxSettings
    private val table: Table = Table()
    private val upperInfoScrollPane: ScrollPane = createScrollPane(table)
    private val bottomCommandInfo: BottomCommandInfo = BottomCommandInfo(game)

    private val infoAndCommand = createSplitPane(upperInfoScrollPane, bottomCommandInfo.getActor(), true)

    private val overviewInfo: OverviewInfo = OverviewInfo(game)
    private val physicsInfo: PhysicsInfo = PhysicsInfo(game)

    init {
        // Add child screen component
        addChildScreenComponent(bottomCommandInfo)
        addChildScreenComponent(overviewInfo)
        addChildScreenComponent(physicsInfo)

        // Set background color
        table.background = assets.getBackgroundColor(0.2f, 0.3f, 0.5f, 1.0f)
        upperInfoScrollPane.fadeScrollBars = false
        upperInfoScrollPane.setFlickScroll(true)

        infoAndCommand.splitAmount = gdxSettings.upperInfoAndBottomCommandSplitAmount
    }

    override fun getActor(): SplitPane {
        return infoAndCommand
    }

    override fun onGdxSettingsChange() {
        // Show info type based on setting
        when (gdxSettings.showingInfoType) {
            ShowingInfoType.OVERVIEW -> upperInfoScrollPane.actor = overviewInfo.getActor()
            ShowingInfoType.PHYSICS -> upperInfoScrollPane.actor = physicsInfo.getActor()
        }

        // Show bottom command or not
        if (gdxSettings.showingBottomCommand) {
            infoAndCommand.splitAmount = gdxSettings.upperInfoAndBottomCommandSplitAmount
        } else {
            gdxSettings.upperInfoAndBottomCommandSplitAmount = infoAndCommand.splitAmount
            infoAndCommand.splitAmount = infoAndCommand.maxSplitAmount
        }
    }
}