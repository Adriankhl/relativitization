package relativitization.universe.ai.defaults.node.self.event

import relativitization.universe.ai.defaults.consideration.diplomacy.HierarchyConsideration
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
        val movementEventKeyList: List<Int> =
            planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData(
            ).playerInternalData.eventDataMap.filter {
                // Filter out MoveToDouble3DEvent
                it.value.event is MoveToDouble3DEvent
            }.keys.toList()

        return movementEventKeyList.map {
            PickMoveToDouble3DEventDualUtilityOption(
                it,
            )
        }
    }
}

/**
 * Cancel all MoveToDouble3D beside event at keepEventIndex
 */
class PickMoveToDouble3DEventDualUtilityOption(
    private val keepEventIndex: Int,
) : DualUtilityOption() {

    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        val movementEventMap: Map<Int, EventData> =
            planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.eventDataMap.filter {
                // Filter out MoveToDouble3DEvent
                it.value.event is MoveToDouble3DEvent
            }

        return if (movementEventMap.containsKey(keepEventIndex)) {
            listOf(
                RelationConsideration(
                    playerId = movementEventMap.getValue(keepEventIndex).event.fromId,
                ),
                HierarchyConsideration(
                    playerId = movementEventMap.getValue(keepEventIndex).event.fromId,
                )
            )
        } else {
            listOf()
        }
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val movementEventMap: Map<Int, EventData> =
            planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.eventDataMap.filter {
                // Filter out MoveToDouble3DEvent
                it.value.event is MoveToDouble3DEvent
            }
        val commandList: List<Command> = movementEventMap.filter { it.key != keepEventIndex }.map {
            SelectEventChoiceCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                eventKey = it.key,
                eventName = it.value.event.name(),
                choice = 1,
            )
        }

        planDataAtPlayer.addAllCommand(commandList)
    }
}