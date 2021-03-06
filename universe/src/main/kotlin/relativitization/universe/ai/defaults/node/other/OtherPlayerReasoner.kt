package relativitization.universe.ai.defaults.node.other

import relativitization.universe.ai.defaults.node.other.construction.ForeignFactoryReasoner
import relativitization.universe.ai.defaults.node.other.diplomacy.DeclareWarReasoner
import relativitization.universe.ai.defaults.node.other.diplomacy.ProposePeaceReasoner
import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import kotlin.random.Random

class OtherPlayerReasoner(random: Random) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return listOf(
            ForeignFactoryReasoner(random),
            ProposePeaceReasoner(random),
            DeclareWarReasoner(random),
        )
    }
}