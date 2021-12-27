package relativitization.universe.ai.defaults.node.self.pop

import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.MutableEconomyData
import relativitization.universe.data.components.defaults.economy.MutableResourceData
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType

class SalaryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
        .popSystemData().carrierDataMap.keys.map { carrierId ->
            PopType.values().map { popType ->
                AdjustSalaryAINode(carrierId, popType)
            }
        }.flatten()
}

class AdjustSalaryAINode(
    val carrierId: Int,
    val popType: PopType,
) : AINode {
    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val commonPopData: MutableCommonPopData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId).allPopData
            .getCommonPopData(popType)

        val resourceData: MutableResourceData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.economyData().resourceData

        // Compute total salary needed to
    }
}