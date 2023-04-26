package relativitization.universe.game.ai.defaults.node.subordinate

import relativitization.universe.core.data.PlanDataAtPlayer
import relativitization.universe.game.ai.defaults.node.subordinate.direct.DirectSubordinateReasoner
import relativitization.universe.game.ai.defaults.utils.AINode
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.game.ai.defaults.utils.SequenceReasoner
import kotlin.random.Random

class SubordinateReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return listOf(
            DirectSubordinateReasoner(random),
        )
    }
}