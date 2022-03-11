package relativitization.universe.ai.defaults.node.other.construction

import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.popSystemData

class ForeignFactoryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        NewForeignFuelFactoryReasoner()
    )
}

class NewForeignFuelFactoryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val neighborList: List<PlayerData> = planDataAtPlayer.universeData3DAtPlayer.getNeighbour(
            1
        )

        return neighborList.map { playerData ->
            playerData.playerInternalData.popSystemData().carrierDataMap.map { (carrierId, _) ->
                NewForeignFuelFactoryAtCarrierReasoner(playerData.playerId, carrierId)
            }
        }.flatten()
    }
}

/**
 * Consider building a fuel factory at a foreign carrier
 */
class NewForeignFuelFactoryAtCarrierReasoner(
    private val playerId: Int,
    private val carrierId: Int,
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        BuildForeignFuelFactoryOption(playerId, carrierId),
        DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0),
    )
}

/**
 * Option to build a foreign fuel factory at a carrier
 */
class BuildForeignFuelFactoryOption(
    private val playerId: Int,
    private val carrierId: Int
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        TODO("Not yet implemented")
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        TODO("Not yet implemented")
    }
}