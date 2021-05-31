package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
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
        // Set background color
        table.background = assets.getBackgroundColor(0.2f, 0.3f, 0.5f, 1.0f)
        upperInfoScrollPane.fadeScrollBars = false
        upperInfoScrollPane.setFlickScroll(true)

        infoAndCommand.splitAmount = gdxSettings
    }

    override fun getActor(): SplitPane {
        return infoAndCommand
    }

    override fun update() {
    }

    fun updateAll() {
        update()
        worldMap.update()
    }

    /**
     * Switch information shown in the info scroll pane, hide info panel if the same showing info is requested
     * for repeat pressing the same button
     */
    fun switchShowingInfo(newShowingInfo: ShowingInfo) {
        if (newShowingInfo == showingInfo) {
            showingInfo = ShowingInfo.HIDE
        } else {
            when (newShowingInfo) {
                ShowingInfo.OVERVIEW -> upperInfoScrollPane.actor = overviewInfo.getActor()
                ShowingInfo.PHYSICS -> upperInfoScrollPane.actor = physicsInfo.getActor()
            }
            showingInfo = newShowingInfo
        }
    }

    /**
     * Switch showing bottom command info or not
     */
    fun switchShowingCommand(newShowingCommand: Boolean) {
        showingCommand = newShowingCommand
        if (showingCommand) {
            infoAndCommand.splitAmount = gdxSettings.infoAndCommandSplitAmount
        } else {
            gdxSettings.infoAndCommandSplitAmount = infoAndCommand.splitAmount
            infoAndCommand.splitAmount = infoAndCommand.maxSplitAmount
        }
    }
}