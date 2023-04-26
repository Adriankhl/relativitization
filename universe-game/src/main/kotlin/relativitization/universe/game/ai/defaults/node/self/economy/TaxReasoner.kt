package relativitization.universe.game.ai.defaults.node.self.economy

import relativitization.universe.core.data.PlanDataAtPlayer
import relativitization.universe.game.ai.defaults.utils.AINode
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.game.ai.defaults.utils.SequenceReasoner
import relativitization.universe.game.data.commands.ChangeHighIncomeTaxCommand
import relativitization.universe.game.data.commands.ChangeLowIncomeTaxCommand
import relativitization.universe.game.data.commands.ChangeLowMiddleBoundaryCommand
import relativitization.universe.game.data.commands.ChangeMiddleHighBoundaryCommand
import relativitization.universe.game.data.commands.ChangeMiddleIncomeTaxCommand

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

class LowMiddleIncomeBoundaryAINode : AINode() {
    override fun updatePlan(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ) {
        if (planDataAtPlayer.getCurrentMutablePlayerData().isTopLeader()) {
            // Set the boundary to a very low value
            planDataAtPlayer.addCommand(
                ChangeLowMiddleBoundaryCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    boundary = 1E-20,
                )
            )
        }
    }
}

class MiddleHighIncomeBoundaryAINode : AINode() {
    override fun updatePlan(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ) {
        if (planDataAtPlayer.getCurrentMutablePlayerData().isTopLeader()) {
            // Set the boundary to a very low value
            planDataAtPlayer.addCommand(
                ChangeMiddleHighBoundaryCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
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
        LowIncomeTaxAINode(),
        MiddleIncomeTaxAINode(),
        HighIncomeTaxAINode(),
    )
}

class LowIncomeTaxAINode : AINode() {
    override fun updatePlan(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ) {
        if (planDataAtPlayer.getCurrentMutablePlayerData().isTopLeader()) {
            planDataAtPlayer.addCommand(
                ChangeLowIncomeTaxCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    rate = 0.001,
                )
            )
        }
    }
}

class MiddleIncomeTaxAINode : AINode() {
    override fun updatePlan(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ) {
        if (planDataAtPlayer.getCurrentMutablePlayerData().isTopLeader()) {
            planDataAtPlayer.addCommand(
                ChangeMiddleIncomeTaxCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    rate = 0.01,
                )
            )
        }
    }
}

class HighIncomeTaxAINode : AINode() {
    override fun updatePlan(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ) {
        if (planDataAtPlayer.getCurrentMutablePlayerData().isTopLeader()) {
            planDataAtPlayer.addCommand(
                ChangeHighIncomeTaxCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    rate = 0.1,
                )
            )
        }
    }
}