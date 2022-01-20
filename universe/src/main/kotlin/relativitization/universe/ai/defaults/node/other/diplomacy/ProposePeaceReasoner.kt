package relativitization.universe.ai.defaults.node.other.diplomacy

import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.ProposePeaceCommand

class ProposePeaceReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.diplomacyData().warData
            .warStateMap.keys.map { ProposePeaceToPlayerReasoner(it) }
    }
}

class ProposePeaceToPlayerReasoner(
    private val targetPlayerId: Int
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        ProposePeaceToPlayerOption(targetPlayerId),
        DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0)
    )
}

class ProposePeaceToPlayerOption(
    private val targetPlayerId: Int
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf()
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        planDataAtPlayer.addCommand(
            ProposePeaceCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                targetPlayerId = targetPlayerId
            )
        )
    }
}