package relativitization.universe.ai.defaults.node.self.economy

import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.*
import kotlin.random.Random

class TaxReasoner(random: Random) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        IncomeBoundaryReasoner(random),
        IncomeTaxReasoner(random),
    )
}

/**
 * Set the boundary of low, middle and high income
 */
class IncomeBoundaryReasoner(random: Random) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        LowMiddleIncomeBoundaryAINode(random),
        MiddleHighIncomeBoundaryAINode(random),
    )
}

class LowMiddleIncomeBoundaryAINode(random: Random) : AINode(random) {
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

class MiddleHighIncomeBoundaryAINode(random: Random) : AINode(random) {
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
class IncomeTaxReasoner(random: Random) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        LowIncomeTaxAINode(random),
        MiddleIncomeTaxAINode(random),
        HighIncomeTaxAINode(random),
    )
}

class LowIncomeTaxAINode(random: Random) : AINode(random) {
    override fun updatePlan(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ) {
        if (planDataAtPlayer.getCurrentMutablePlayerData().isTopLeader()) {
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
}

class MiddleIncomeTaxAINode(random: Random) : AINode(random) {
    override fun updatePlan(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ) {
        if (planDataAtPlayer.getCurrentMutablePlayerData().isTopLeader()) {
            planDataAtPlayer.addCommand(
                ChangeMiddleIncomeTaxCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                    rate = 0.01,
                )
            )
        }
    }
}

class HighIncomeTaxAINode(random: Random) : AINode(random) {
    override fun updatePlan(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ) {
        if (planDataAtPlayer.getCurrentMutablePlayerData().isTopLeader()) {
            planDataAtPlayer.addCommand(
                ChangeHighIncomeTaxCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                    rate = 0.1,
                )
            )
        }
    }
}