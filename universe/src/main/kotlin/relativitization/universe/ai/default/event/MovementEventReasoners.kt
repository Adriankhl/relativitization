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
class PickMoveToDouble3DEventReasoner(
    private val planDataAtPlayer: PlanDataAtPlayer
) : DualUtilityReasoner() {

    private val movementEventKeyList: List<Int> =
        planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData(
        ).playerInternalData.eventDataMap.filter {
            // Filter out MoveToDouble3DEvent
            it.value.event is MoveToDouble3DEvent
        }.keys.toList()


    override fun getOptionList(): List<Option> {
        return movementEventKeyList.map {
            PickMoveToDouble3DEventOption(
                planDataAtPlayer,
                it,
            )
        }
    }

    override fun getConsiderationList(): List<Consideration> = listOf()
}

/**
 * Cancel all MoveToDouble3D beside event at keepEventIndex
 */
class PickMoveToDouble3DEventOption(
    private val planDataAtPlayer: PlanDataAtPlayer,
    private val keepEventIndex: Int,
) : CommandListOption(planDataAtPlayer) {

    private val movementEventMap: Map<Int, EventData> =
        planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.eventDataMap.filter {
            // Filter out MoveToDouble3DEvent
            it.value.event is MoveToDouble3DEvent
        }

    override fun getCommandList(): List<Command> {
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

    override fun getConsiderationList(): List<Consideration> {
        return if (movementEventMap.containsKey(keepEventIndex)) {
            listOf(
                RelationConsideration(
                    playerId = movementEventMap.getValue(keepEventIndex).event.fromId,
                    diplomacyData = planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.diplomacyData(),
                )
            )
        } else {
            listOf()
        }
    }
}