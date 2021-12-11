package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import relativitization.game.RelativitizationGame
import relativitization.game.ShowingInfoType
import relativitization.game.components.bottom.BottomCommandInfo
import relativitization.game.components.upper.*
import relativitization.game.utils.ScreenComponent

class GameScreenInfo(val game: RelativitizationGame) : ScreenComponent<SplitPane>(game.assets) {
    private val gdxSettings = game.gdxSettings

    private val aiInfo: AIInfo = AIInfo(game)
    private val playersInfo: PlayersInfo = PlayersInfo(game)
    private val overviewInfo: OverviewInfo = OverviewInfo(game)
    private val physicsInfo: PhysicsInfo = PhysicsInfo(game)
    private val eventsInfo: EventsInfo = EventsInfo(game)
    private val commandsInfo: CommandsInfo = CommandsInfo(game)
    private val popSystemInfo: PopSystemInfo = PopSystemInfo(game)
    private val knowledgeMapInfo: KnowledgeMapInfo = KnowledgeMapInfo(game)
    private val scienceInfo: ScienceInfo = ScienceInfo(game)
    private val politicsInfo: PoliticsInfo = PoliticsInfo(game)
    private val diplomacyInfo: DiplomacyInfo = DiplomacyInfo(game)
    private val economyInfo: EconomyInfo = EconomyInfo(game)
    private val modifierInfo: ModifierInfo = ModifierInfo(game)
    private val mapModeInfo: MapModeInfo = MapModeInfo(game)

    private val upperInfoContainer: Container<Actor> = Container(overviewInfo.getScreenComponent())
    private val bottomCommandInfo: BottomCommandInfo = BottomCommandInfo(game)

    private val infoAndCommand = createSplitPane(
        upperInfoContainer,
        bottomCommandInfo.getScreenComponent(),
        true
    )


    init {
        // Add child screen component
        addChildScreenComponent(bottomCommandInfo)
        addChildScreenComponent(aiInfo)
        addChildScreenComponent(playersInfo)
        addChildScreenComponent(overviewInfo)
        addChildScreenComponent(physicsInfo)
        addChildScreenComponent(eventsInfo)
        addChildScreenComponent(commandsInfo)
        addChildScreenComponent(popSystemInfo)
        addChildScreenComponent(knowledgeMapInfo)
        addChildScreenComponent(scienceInfo)
        addChildScreenComponent(politicsInfo)
        addChildScreenComponent(diplomacyInfo)
        addChildScreenComponent(economyInfo)
        addChildScreenComponent(modifierInfo)
        addChildScreenComponent(mapModeInfo)

        upperInfoContainer.fill()

        infoAndCommand.splitAmount = gdxSettings.upperInfoAndBottomCommandSplitAmount
    }

    override fun getScreenComponent(): SplitPane {
        return infoAndCommand
    }

    override fun onGdxSettingsChange() {
        // Show info type based on setting
        when (gdxSettings.showingInfoType) {
            ShowingInfoType.AI -> upperInfoContainer.actor = aiInfo.getScreenComponent()
            ShowingInfoType.PLAYERS -> upperInfoContainer.actor = playersInfo.getScreenComponent()
            ShowingInfoType.OVERVIEW -> upperInfoContainer.actor = overviewInfo.getScreenComponent()
            ShowingInfoType.PHYSICS -> upperInfoContainer.actor = physicsInfo.getScreenComponent()
            ShowingInfoType.EVENTS -> upperInfoContainer.actor = eventsInfo.getScreenComponent()
            ShowingInfoType.COMMANDS -> upperInfoContainer.actor = commandsInfo.getScreenComponent()
            ShowingInfoType.POP_SYSTEM -> upperInfoContainer.actor = popSystemInfo.getScreenComponent()
            ShowingInfoType.KNOWLEDGE_MAP -> upperInfoContainer.actor = knowledgeMapInfo.getScreenComponent()
            ShowingInfoType.SCIENCE -> upperInfoContainer.actor = scienceInfo.getScreenComponent()
            ShowingInfoType.POLITICS -> upperInfoContainer.actor = politicsInfo.getScreenComponent()
            ShowingInfoType.DIPLOMACY -> upperInfoContainer.actor = diplomacyInfo.getScreenComponent()
            ShowingInfoType.ECONOMY -> upperInfoContainer.actor = economyInfo.getScreenComponent()
            ShowingInfoType.MODIFIER -> upperInfoContainer.actor = modifierInfo.getScreenComponent()
            ShowingInfoType.MAP_MODE -> upperInfoContainer.actor = mapModeInfo.getScreenComponent()
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