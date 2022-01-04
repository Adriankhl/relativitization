package relativitization.universe.ai.defaults.node.self.construction

import relativitization.universe.ai.defaults.consideration.building.NoInstituteAtCarrierConsideration
import relativitization.universe.ai.defaults.consideration.building.SufficientInstituteConsideration
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
            NewInstituteAtCarrierOption(carrierId),
            DoNothingDualUtilityOption(1, 1.0, 1.0)
        )
    }
}

class NewInstituteAtCarrierOption(
    private val carrierId: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf(
            NoInstituteAtCarrierConsideration(
                carrierId = carrierId,
                rankIfTrue = 5,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 1.0
            ),
            SufficientInstituteConsideration(
                carrierId = carrierId,
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 0.1,
                rankIfFalse = 1,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 1.0
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
    }
}