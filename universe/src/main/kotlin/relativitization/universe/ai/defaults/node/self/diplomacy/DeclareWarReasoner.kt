package relativitization.universe.ai.defaults.node.self.diplomacy

import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer

class DeclareWarReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        SpaceConflictDeclareWarReasoner(),
    )
}

/**
 * Declare war due to space conflict
 */
class SpaceConflictDeclareWarReasoner : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0)
        )
    }
}