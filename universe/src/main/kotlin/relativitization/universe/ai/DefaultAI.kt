package relativitization.universe.ai

import relativitization.universe.ai.defaults.node.ai.RecordRecentlySentCommandAINode
import relativitization.universe.ai.defaults.node.event.EventReasoner
import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.RelativitizationLogManager

object DefaultAI : AI() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        logger.debug("Computing player ${universeData3DAtPlayer.id} with DefaultAI")
        return RootReasoner(universeData3DAtPlayer).computeCommandList()
    }
}

class RootReasoner(
    universeData3DAtPlayer: UniverseData3DAtPlayer
) : SequenceReasoner() {

    private val planDataAtPlayer: PlanDataAtPlayer = universeData3DAtPlayer.getPlanDataAtPlayer {}
    private val planState: PlanState = PlanState()

    fun computeCommandList(): List<Command> {
        logger.debug("Root reasoner computing commandList")
        updatePlan(planDataAtPlayer, planState)
        return planDataAtPlayer.commandList
    }

    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        EventReasoner(),
        RecordRecentlySentCommandAINode(),
    )

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}