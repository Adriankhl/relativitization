package relativitization.universe.game.ai.defaults.node.self.event

import relativitization.universe.core.data.PlanDataAtPlayer
import relativitization.universe.core.data.events.MutableEventData
import relativitization.universe.game.ai.defaults.consideration.diplomacy.RelationConsideration
import relativitization.universe.game.ai.defaults.consideration.diplomacy.TooManyAllyConsideration
import relativitization.universe.game.ai.defaults.utils.AINode
import relativitization.universe.game.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.game.ai.defaults.utils.DualUtilityOption
import relativitization.universe.game.ai.defaults.utils.DualUtilityReasoner
import relativitization.universe.game.ai.defaults.utils.PlainDualUtilityConsideration
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.game.ai.defaults.utils.SequenceReasoner
import relativitization.universe.game.data.commands.SelectEventChoiceCommand
import relativitization.universe.game.data.events.ProposeAllianceEvent
import kotlin.random.Random

/**
 * Contains all reasoner related to alliance
 */
class AllianceEventReasoner(
    private val eventNameKeyMap: Map<String, List<Int>>,
    private val random: Random,
) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        AllProposeAllianceEventReasoner(eventNameKeyMap, random),
    )
}

class AllProposeAllianceEventReasoner(
    private val eventNameKeyMap: Map<String, List<Int>>,
    private val random: Random,
) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val eventKeyList: List<Int> = eventNameKeyMap.getOrDefault(
            ProposeAllianceEvent::class.keyName(),
            listOf(),
        )

        return eventKeyList.map {
            ProposeAllianceEventReasoner(it, random)
        }
    }
}

class ProposeAllianceEventReasoner(
    private val eventKey: Int,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            AcceptAllianceOption(eventKey),
            RejectAllianceOption(eventKey),
        )
    }
}

class AcceptAllianceOption(
    private val eventKey: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        val eventData: MutableEventData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.eventDataMap.getValue(eventKey)
        return listOf(
            TooManyAllyConsideration(
                playerId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                targetNumAlly = 2,
                rankIfTrue = 0,
                multiplierIfTrue = 0.0,
                bonusIfTrue = 0.0,
                rankIfFalse = 1,
                multiplierIfFalse = 0.1,
                bonusIfFalse = 1.0,
            ),
            RelationConsideration(
                otherPlayerId = eventData.fromId,
                initialMultiplier = 1.0,
                exponent = 1.2,
                rank = 1,
                bonus = 0.0,
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        planDataAtPlayer.addCommand(
            SelectEventChoiceCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                eventKey = eventKey,
                choice = 0,
            )
        )
    }
}

class RejectAllianceOption(
    private val eventKey: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf(
            PlainDualUtilityConsideration(rank = 1, multiplier = 1.0, bonus = 1.0)
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        planDataAtPlayer.addCommand(
            SelectEventChoiceCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                eventKey = eventKey,
                choice = 1,
            )
        )
    }
}