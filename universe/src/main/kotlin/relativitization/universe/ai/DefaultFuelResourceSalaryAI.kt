package relativitization.universe.ai

import relativitization.universe.ai.defaults.node.self.pop.SalaryReasoner
import relativitization.universe.ai.defaults.node.self.storage.BalanceFuelReasoner
import relativitization.universe.ai.defaults.node.self.storage.BalanceResourceReasoner
import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.RelativitizationLogManager

object DefaultFuelResourceSalaryAI : AI() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        logger.debug("Computing player ${universeData3DAtPlayer.id} with DefaultAI")
        return SelfManagementAIRootReasoner(universeData3DAtPlayer).computeCommandList()
    }
}

class SelfManagementAIRootReasoner(
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
        BalanceFuelReasoner(),
        BalanceResourceReasoner(),
        SalaryReasoner(),
    )

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}