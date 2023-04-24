package relativitization.universe.ai.defaults.node.self.politics

import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.ChangeFactoryPolicyCommand

class PolicyReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        FactoryPolicyAINode()
    )
}

class FactoryPolicyAINode : AINode() {
    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        // Only top leader can change policy
        if (planDataAtPlayer.getCurrentPlayerData().isTopLeader()) {
            planDataAtPlayer.addCommand(
                ChangeFactoryPolicyCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    allowSubordinateBuildFactory = true,
                    allowLeaderBuildLocalFactory = false,
                    allowForeignInvestor = false,
                )
            )
        }
    }
}