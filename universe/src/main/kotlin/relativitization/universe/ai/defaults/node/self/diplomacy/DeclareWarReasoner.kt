package relativitization.universe.ai.defaults.node.self.diplomacy

import relativitization.universe.ai.defaults.consideration.military.LargerMilitaryStrengthConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.defaults.physics.Int3D

class DeclareWarReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        SpaceConflictReasoner(),
    )
}

/**
 * Declare war due to space conflict
 */
class SpaceConflictReasoner : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {

        val subordinateInt3DSet: Set<Int3D> = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .subordinateIdList.map {
                planDataAtPlayer.universeData3DAtPlayer.get(it).int4D.toInt3D()
            }.toSet()

        val allNeighbourInt3DSet: Set<Int3D> = subordinateInt3DSet.flatMap {
            it.getInt3DCubeList(
                halfEdgeLength = 1,
                minX = 0,
                maxX = planDataAtPlayer.universeData3DAtPlayer.universeSettings.xDim - 1,
                minY = 0,
                maxY = planDataAtPlayer.universeData3DAtPlayer.universeSettings.yDim - 1,
                minZ = 0,
                maxZ = planDataAtPlayer.universeData3DAtPlayer.universeSettings.zDim - 1,
            )
        }.toSet()

        val currentPlayer: MutablePlayerData = planDataAtPlayer.getCurrentMutablePlayerData()

        val conflictPlayerIdList: List<Int> = allNeighbourInt3DSet.flatMap {
            planDataAtPlayer.universeData3DAtPlayer.playerId3DMap[it.x][it.y][it.z].values.flatten()
        }.filter {
            !currentPlayer.isLeaderOrSelf(it) && !currentPlayer.isSubOrdinate(it)
        }

        val declareWarOptionList: List<SpaceConflictDeclareWarOption> = conflictPlayerIdList.map {
            SpaceConflictDeclareWarOption(it)
        }

        return declareWarOptionList + listOf(
            DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0)
        )
    }
}

/**
 * Declare war on target player due to space conflict
 */
class SpaceConflictDeclareWarOption(
    private val targetPlayerId: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf(
            LargerMilitaryStrengthConsideration(
                targetPlayerId = targetPlayerId,
                rankIfTrue = 1,
                multiplierIfTrue = 0.01,
                bonusIfTrue = 1.0,
                rankIfFalse = 1,
                multiplierIfFalse = 0.00001,
                bonusIfFalse = 1.0
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
    }
}