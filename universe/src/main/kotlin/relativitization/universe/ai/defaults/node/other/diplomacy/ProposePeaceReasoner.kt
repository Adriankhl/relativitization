package relativitization.universe.ai.defaults.node.other.diplomacy

import relativitization.universe.ai.defaults.consideration.military.WarLossConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.events.ProposePeaceEvent
import kotlin.random.Random

class ProposePeaceReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.diplomacyData()
            .relationData.selfWarDataMap.keys.map { ProposePeaceToPlayerReasoner(it, random) }
    }
}

class ProposePeaceToPlayerReasoner(
    private val targetPlayerId: Int,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        ProposePeaceToPlayerOption(targetPlayerId),
        DoNothingDualUtilityOption(
            rank = 1,
            multiplier = 1.0,
            bonus = 1.0,
        )
    )
}

class ProposePeaceToPlayerOption(
    private val targetPlayerId: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> = listOf(
        WarLossConsideration(
            otherPlayerId = targetPlayerId,
            minMultiplier = 0.0,
            maxMultiplier = 2.0,
            rank = 1,
            bonus = 1.0,
        ),
    )

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val proposePeaceEvent = ProposePeaceEvent(
            toId = targetPlayerId,
            fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId
        )
        planDataAtPlayer.addCommand(
            AddEventCommand(
                event = proposePeaceEvent,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
            )
        )
    }
}