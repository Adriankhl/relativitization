package relativitization.universe.ai.default

import relativitization.universe.ai.AI
import relativitization.universe.ai.default.event.EventReasoner
import relativitization.universe.ai.default.event.EventReasonerOption
import relativitization.universe.ai.default.utils.DecisionData
import relativitization.universe.ai.default.utils.Option
import relativitization.universe.ai.default.utils.SequenceReasoner
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.RelativitizationLogManager

object DefaultAI : AI() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        logger.debug("Computing player ${universeData3DAtPlayer.id} with DefaultAI")
        return RootReasoner(universeData3DAtPlayer).getCommandList()
    }
}

class RootReasoner(universeData3DAtPlayer: UniverseData3DAtPlayer) : SequenceReasoner() {

    val decisionData = DecisionData(universeData3DAtPlayer)

    override val optionList: List<Option> = listOf(
        EventReasonerOption(decisionData),
    )
}