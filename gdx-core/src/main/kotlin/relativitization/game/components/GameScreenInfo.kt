package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import relativitization.game.RelativitizationGame
import relativitization.game.ShowingInfoType
import relativitization.game.components.info.*
import relativitization.game.utils.ScreenComponent

class GameScreenInfo(val game: RelativitizationGame) : ScreenComponent<SplitPane>(game.assets) {
    private val gdxSettings = game.gdxSettings

    private val playersInfo: PlayersInfo = PlayersInfo(game)
    private val overviewInfo: OverviewInfo = OverviewInfo(game)
    private val physicsInfo: PhysicsInfo = PhysicsInfo(game)
    private val eventsInfo: EventsInfo = EventsInfo(game)
    private val commandsInfo: CommandsInfo = CommandsInfo(game)

    private val upperInfoScrollPane: ScrollPane = createScrollPane(overviewInfo.getScreenComponent())
    private val bottomCommandInfo: BottomCommandInfo = BottomCommandInfo(game)

    private val infoAndCommand = createSplitPane(upperInfoScrollPane, bottomCommandInfo.getScreenComponent(), true)


    init {
        // Add child screen component
        addChildScreenComponent(bottomCommandInfo)
        addChildScreenComponent(playersInfo)
        addChildScreenComponent(overviewInfo)
        addChildScreenComponent(physicsInfo)

        // Set background color
        upperInfoScrollPane.fadeScrollBars = false
        upperInfoScrollPane.setClamp(true)
        upperInfoScrollPane.setOverscroll(false, false)

        infoAndCommand.splitAmount = gdxSettings.upperInfoAndBottomCommandSplitAmount
    }

    override fun getScreenComponent(): SplitPane {
        return infoAndCommand
    }

    override fun onGdxSettingsChange() {
        // Show info type based on setting
        when (gdxSettings.showingInfoType) {
            ShowingInfoType.PLAYERS -> upperInfoScrollPane.actor = playersInfo.getScreenComponent()
            ShowingInfoType.OVERVIEW -> upperInfoScrollPane.actor = overviewInfo.getScreenComponent()
            ShowingInfoType.PHYSICS -> upperInfoScrollPane.actor = physicsInfo.getScreenComponent()
            ShowingInfoType.EVENTS -> upperInfoScrollPane.actor = eventsInfo.getScreenComponent()
            ShowingInfoType.COMMANDS -> upperInfoScrollPane.actor = commandsInfo.getScreenComponent()
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