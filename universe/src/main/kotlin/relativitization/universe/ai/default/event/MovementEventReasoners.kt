package relativitization.universe.ai.default.event

import relativitization.universe.ai.default.consideration.RelationConsideration
import relativitization.universe.ai.default.utils.*
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
    ): List<Option> {
        val movementEventKeyList: List<Int> =
            planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData(
            ).playerInternalData.eventDataMap.filter {
                // Filter out MoveToDouble3DEvent
                it.value.event is MoveToDouble3DEvent
            }.keys.toList()

        return movementEventKeyList.map {
            PickMoveToDouble3DEventOption(
                it,
            )
        }
    }
}

/**
 * Cancel all MoveToDouble3D beside event at keepEventIndex
 */
class PickMoveToDouble3DEventOption(
    private val keepEventIndex: Int,
) : Option() {

    override fun getCommandList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<Command> {
        val movementEventMap: Map<Int, EventData> =
            planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.eventDataMap.filter {
                // Filter out MoveToDouble3DEvent
                it.value.event is MoveToDouble3DEvent
            }
        return movementEventMap.filter { it.key != keepEventIndex }.map {
            SelectEventChoiceCommand(
                eventKey = it.key,
                eventName = it.value.event.name(),
                choice = 1,
                fromId = planDataAtPlayer.universeData3DAtPlayer.id,
                fromInt4D = planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData().int4D,
                toId = planDataAtPlayer.universeData3DAtPlayer.id,
            )
        }
    }

    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<Consideration> {
        val movementEventMap: Map<Int, EventData> =
            planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.eventDataMap.filter {
                // Filter out MoveToDouble3DEvent
                it.value.event is MoveToDouble3DEvent
            }

        return if (movementEventMap.containsKey(keepEventIndex)) {
            listOf(
                RelationConsideration(
                    playerId = movementEventMap.getValue(keepEventIndex).event.fromId,
                )
            )
        } else {
            listOf()
        }
    }
}