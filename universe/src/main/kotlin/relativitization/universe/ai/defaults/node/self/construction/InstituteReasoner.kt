package relativitization.universe.ai.defaults.node.self.construction

import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer

class InstituteReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return listOf(NewInstituteReasoner())
    }
}

/**
 * Consider building new institutes at all carrier
 */
class NewInstituteReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
        .popSystemData().carrierDataMap.keys.map {
            NewInstituteAtCarrierReasoner(it)
        }
}

/**
 * Consider building a new institute at a carrier
 */
class NewInstituteAtCarrierReasoner(
    private val carrierId: Int,
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            DoNothingDualUtilityOption(1, 1.0, 1.0)
        )
    }
}