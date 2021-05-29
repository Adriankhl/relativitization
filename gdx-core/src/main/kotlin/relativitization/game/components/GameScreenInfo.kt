package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.components.info.BottomCommandInfo
import relativitization.game.components.info.OverviewInfo
import relativitization.game.components.info.PhysicsInfo
import relativitization.game.utils.ScreenComponent

class GameScreenInfo(
    val game: RelativitizationGame,
    val worldMap: GameScreenWorldMap
) : ScreenComponent<SplitPane>(game.assets) {
    private val gdxSetting = game.gdxSetting
    private val table: Table = Table()
    private val upperInfoScrollPane: ScrollPane = createScrollPane(table)
    private val bottomCommandInfo: BottomCommandInfo = BottomCommandInfo(game, worldMap, this)

    private val infoAndCommand = createSplitPane(upperInfoScrollPane, bottomCommandInfo.get(), true)


    private val overviewInfo: OverviewInfo = OverviewInfo(game, worldMap, this)
    private val physicsInfo: PhysicsInfo = PhysicsInfo(game, worldMap, this)
    var showingInfo: ShowingInfo = ShowingInfo.OVERVIEW


    init {
        // Set background color
        table.background = assets.getBackgroundColor(0.2f, 0.3f, 0.5f, 1.0f)
        upperInfoScrollPane.fadeScrollBars = false
        upperInfoScrollPane.setFlickScroll(true)

        infoAndCommand.splitAmount = gdxSetting.infoAndCommandSplitAmount
    }

    override fun get(): SplitPane {
        return infoAndCommand
    }

    override fun update() {
    }

    fun updateAll() {
        update()
        worldMap.update()
    }

    fun switchShowingInfo() {
        when (showingInfo) {
            ShowingInfo.OVERVIEW -> upperInfoScrollPane.actor = overviewInfo.get()
            ShowingInfo.PHYSICS -> upperInfoScrollPane.actor = physicsInfo.get()
        }
    }
}

enum class ShowingInfo {
    OVERVIEW,
    PHYSICS
}