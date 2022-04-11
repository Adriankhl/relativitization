package relativitization.universe.ai.defaults.node.self.event

import relativitization.universe.ai.defaults.consideration.hierarchy.HierarchyRelationConsideration
import relativitization.universe.ai.defaults.consideration.diplomacy.RelationConsideration
import relativitization.universe.ai.defaults.consideration.event.HasMovementTargetConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.SelectEventChoiceCommand
import relativitization.universe.data.events.MoveToDouble3DEvent
import relativitization.universe.data.events.MutableEventData
import relativitization.universe.data.events.name

/**
 * Reasoner to pick only one MoveToDouble3D event
 */
class PickMoveToDouble3DEventReasoner : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        val movementEventKeySet: Set<Int> = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.eventDataMap.filterValues {
                // Filter out MoveToDouble3DEvent
                it.event is MoveToDouble3DEvent
            }.keys

        return movementEventKeySet.map {
            PickMoveToDouble3DEventDualUtilityOption(
                eventKey = it,
                otherEventKeySet = movementEventKeySet - it
            )
        } + DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0)
    }
}

/**
 * Cancel all MoveToDouble3D event beside event at keepEventIndex
 *
 * @property eventKey keep the event with this key
 * @property otherEventKeySet drop the event with key contain in this set
 */
class PickMoveToDouble3DEventDualUtilityOption(
    private val eventKey: Int,
    private val otherEventKeySet: Set<Int>,
) : DualUtilityOption() {

    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {

        val movementEventData: MutableEventData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.eventDataMap.getValue(eventKey)

        return listOf(
            HierarchyRelationConsideration(
                otherPlayerId = movementEventData.event.fromId,
                rankIfSelf = 3,
                rankIfDirectLeader = 4,
                rankIfOtherLeader = 2,
                rankIfDirectSubordinate = 1,
                rankIfOtherSubordinate = 1,
                rankIfOther = 1,
                multiplier = 1.0,
                bonus = 1.0,
            ),
            RelationConsideration(
                otherPlayerId = movementEventData.event.fromId,
                initialMultiplier = 1.0,
                exponent = 1.01,
                rank = 0,
                bonus = 0.0,
            ),
            HasMovementTargetConsideration(
                rankIfTrue = 0,
                multiplierIfTrue = 0.0,
                bonusIfTrue = 0.0,
                rankIfFalse = 0,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 0.0
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val commandList: List<Command> = otherEventKeySet.map {
            SelectEventChoiceCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                eventKey = it,
                eventName = MoveToDouble3DEvent::class.name(),
                choice = 1,
            )
        } + SelectEventChoiceCommand(
            toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
            fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
            fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
            eventKey = eventKey,
            eventName = MoveToDouble3DEvent::class.name(),
            choice = 0,
        )

        planDataAtPlayer.addAllCommand(commandList)
    }
}