package relativitization.universe.ai.defaults.node.self.event

import relativitization.universe.ai.defaults.consideration.hierarchy.HierarchyRelationConsideration
import relativitization.universe.ai.defaults.consideration.diplomacy.RelationConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityOption
import relativitization.universe.ai.defaults.utils.DualUtilityReasoner
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.SelectEventChoiceCommand
import relativitization.universe.data.events.EventData
import relativitization.universe.data.events.MoveToDouble3DEvent
import relativitization.universe.data.events.name

/**
 * Reasoner to pick only one MoveToDouble3D event
 */
class PickMoveToDouble3DEventReasoner : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        val movementEventKeySet: Set<Int> = planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData()
            .playerInternalData.eventDataMap.filter {
                // Filter out MoveToDouble3DEvent
                it.value.event is MoveToDouble3DEvent
            }.keys

        return movementEventKeySet.map {
            PickMoveToDouble3DEventDualUtilityOption(
                eventKey = it,
                otherEventKeySet = movementEventKeySet - it
            )
        }
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

        val movementEventData: EventData = planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData()
            .playerInternalData.eventDataMap.getValue(eventKey)

        return listOf(
            HierarchyRelationConsideration(
                otherPlayerId = movementEventData.event.fromId,
                rankIfSelf = 4,
                rankIfDirectLeader = 3,
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
        }

        planDataAtPlayer.addAllCommand(commandList)
    }
}