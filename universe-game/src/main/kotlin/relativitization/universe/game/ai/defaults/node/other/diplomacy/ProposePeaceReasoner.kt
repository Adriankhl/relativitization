package relativitization.universe.game.ai.defaults.node.other.diplomacy

import relativitization.universe.game.ai.defaults.consideration.military.WarLossConsideration
import relativitization.universe.game.ai.defaults.utils.AINode
import relativitization.universe.game.ai.defaults.utils.DoNothingDualUtilityOption
import relativitization.universe.game.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.game.ai.defaults.utils.DualUtilityOption
import relativitization.universe.game.ai.defaults.utils.DualUtilityReasoner
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.game.ai.defaults.utils.SequenceReasoner
import relativitization.universe.core.data.PlanDataAtPlayer
import relativitization.universe.game.data.commands.AddEventCommand
import relativitization.universe.game.data.components.diplomacyData
import relativitization.universe.game.data.events.ProposePeaceEvent
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
        )
        planDataAtPlayer.addCommand(
            AddEventCommand(
                event = proposePeaceEvent,
            )
        )
    }
}