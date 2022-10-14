package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.Actor
import relativitization.game.RelativitizationGame
import relativitization.game.components.upper.defaults.AIInfoPane
import relativitization.game.components.upper.defaults.CommandsInfoPane
import relativitization.game.components.upper.defaults.DiplomacyInfoPane
import relativitization.game.components.upper.defaults.EconomyInfoPane
import relativitization.game.components.upper.defaults.EventsInfoPane
import relativitization.game.components.upper.defaults.KnowledgeMapInfoPane
import relativitization.game.components.upper.defaults.MapModeInfoPane
import relativitization.game.components.upper.defaults.ModifierInfoPane
import relativitization.game.components.upper.defaults.OverviewInfoPane
import relativitization.game.components.upper.defaults.PhysicsInfoPane
import relativitization.game.components.upper.defaults.PlayersInfoPane
import relativitization.game.components.upper.defaults.PoliticsInfoPane
import relativitization.game.components.upper.defaults.PopSystemInfoPane
import relativitization.game.components.upper.defaults.ScienceInfoPane

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