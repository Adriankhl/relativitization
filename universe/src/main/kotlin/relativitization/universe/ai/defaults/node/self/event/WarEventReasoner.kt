package relativitization.universe.ai.defaults.node.self.event

import relativitization.universe.ai.defaults.consideration.military.WarLossConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.SelectEventChoiceCommand
import relativitization.universe.data.events.MutableEventData
import relativitization.universe.data.events.ProposePeaceEvent
import relativitization.universe.data.events.name

class WarEventReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        AllProposePeaceEventReasoner()
    )
}

class AllProposePeaceEventReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val eventKeySet: Set<Int> = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.eventDataMap.filterValues {
                it.event is ProposePeaceEvent
            }.keys

        return eventKeySet.map {
            ProposePeaceEventReasoner(it)
        }
    }
}

class ProposePeaceEventReasoner(
    private val eventKey: Int
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        AcceptPeaceOption(eventKey),
        RejectPeaceOption(eventKey),
    )
}

class AcceptPeaceOption(
    private val eventKey: Int
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        val peaceEventData: MutableEventData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.eventDataMap.getValue(eventKey)

        return listOf(
            WarLossConsideration(
                otherPlayerId = peaceEventData.event.fromId,
                minMultiplier = 0.0,
                maxMultiplier = 2.0,
                rank = 1,
                bonus = 1.0
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        planDataAtPlayer.addCommand(
            SelectEventChoiceCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                eventKey = eventKey,
                eventName = ProposePeaceEvent::class.name(),
                choice = 0,
            )
        )
    }
}

class RejectPeaceOption(
    private val eventKey: Int
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
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                eventKey = eventKey,
                eventName = ProposePeaceEvent::class.name(),
                choice = 1,
            )
        )
    }
}