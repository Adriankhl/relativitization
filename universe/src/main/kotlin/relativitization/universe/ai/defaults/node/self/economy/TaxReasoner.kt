package relativitization.universe.ai.defaults.node.self.economy

import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.ChangeLowIncomeTaxCommand
import relativitization.universe.data.commands.ChangeLowMiddleBoundaryCommand
import relativitization.universe.data.commands.ChangeMiddleHighBoundaryCommand

class TaxReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        IncomeBoundaryReasoner(),
        IncomeTaxReasoner(),
    )
}

/**
 * Set the boundary of low, middle and high income
 */
class IncomeBoundaryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        LowMiddleIncomeBoundaryAINode(),
        MiddleHighIncomeBoundaryAINode(),
    )
}

class LowMiddleIncomeBoundaryAINode : AINode {
    override fun updatePlan(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ) {
        if (planDataAtPlayer.getCurrentMutablePlayerData().isTopLeader()) {
            // Set the boundary to a very low value
            planDataAtPlayer.addCommand(
                ChangeLowMiddleBoundaryCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                    boundary = 1E-20,
                )
            )
        }
    }
}

class MiddleHighIncomeBoundaryAINode : AINode {
    override fun updatePlan(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ) {
        if (planDataAtPlayer.getCurrentMutablePlayerData().isTopLeader()) {
            // Set the boundary to a very low value
            planDataAtPlayer.addCommand(
                ChangeMiddleHighBoundaryCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                    boundary = 1E-15,
                )
            )
        }
    }
}

/**
 * Set the income tax
 */
class IncomeTaxReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        LowIncomeTaxAINode()
    )
}

class LowIncomeTaxAINode : AINode {
    override fun updatePlan(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ) {
        planDataAtPlayer.addCommand(
            ChangeLowIncomeTaxCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                rate = 0.001,
            )
        )
    }
}