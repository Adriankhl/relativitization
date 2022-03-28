package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.Actor
import relativitization.game.RelativitizationGame
import relativitization.game.components.upper.defaults.*

object DefaultUpperInfoPaneList : UpperInfoPaneList() {
    override fun getUpperInfoPaneList(
        game: RelativitizationGame
    ): List<UpperInfoPane<Actor>> = listOf(
        OverviewInfoPane(game),
        AIInfoPane(game),
        PlayersInfoPane(game),
        PhysicsInfoPane(game),
        EventsInfoPane(game),
        CommandsInfoPane(game),
        PopSystemInfoPane(game),
        KnowledgeMapInfoPane(game),
        ScienceInfoPane(game),
        PoliticsInfoPane(game),
        DiplomacyInfoPane(game),
        EconomyInfoPane(game),
        ModifierInfoPane(game),
        MapModeInfoPane(game),
    )
}