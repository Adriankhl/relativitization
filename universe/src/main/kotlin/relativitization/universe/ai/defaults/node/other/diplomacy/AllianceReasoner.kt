package relativitization.universe.ai.defaults.node.other.diplomacy

import relativitization.universe.ai.defaults.consideration.diplomacy.RelationConsideration
import relativitization.universe.ai.defaults.consideration.diplomacy.TooManyAllyConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.RemoveAllyCommand
import relativitization.universe.data.components.diplomacyData
import kotlin.random.Random

class AllianceReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return listOf(
            RemoveAllyReasoner(random)
        )
    }
}

/**
 * Consider breaking alliance
 */
class RemoveAllyReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        // Filter out non-existing (dead) ally
        val allyIdList: List<Int> = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.diplomacyData().relationData.allyMap.keys.filter {
                planDataAtPlayer.universeData3DAtPlayer.playerDataMap.containsKey(it)
            }

        return allyIdList.map {
            RemoveSpecificAllyReasoner(it, random)
        }
    }
}

class RemoveSpecificAllyReasoner(
    private val otherPlayerId: Int,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            RemoveSpecificAllyOption(otherPlayerId),
            DoNothingDualUtilityOption(
                rank = 1,
                multiplier = 1.0,
                bonus = 1.0,
            )
        )
    }
}

class RemoveSpecificAllyOption(
    private val otherPlayerId: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf(
            TooManyAllyConsideration(
                playerId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                targetNumAlly = 3,
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 1.0,
                rankIfFalse = 1,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 0.01,
            ),
            TooManyAllyConsideration(
                playerId = otherPlayerId,
                targetNumAlly = 3,
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 1.0,
                rankIfFalse = 1,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 0.01,
            ),
            RelationConsideration(
                otherPlayerId = otherPlayerId,
                initialMultiplier = 1.0,
                exponent = 0.99,
                rank = 1,
                bonus = 0.0,
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        planDataAtPlayer.addCommand(
            RemoveAllyCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                targetPlayerId = otherPlayerId,
            )
        )
    }
}