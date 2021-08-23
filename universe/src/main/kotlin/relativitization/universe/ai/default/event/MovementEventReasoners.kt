package relativitization.universe.ai.default.event

import relativitization.universe.ai.default.consideration.RelationConsideration
import relativitization.universe.ai.default.utils.*
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.SelectEventChoiceCommand
import relativitization.universe.data.events.EventData
import relativitization.universe.data.events.EventName

/**
 * Reasoner to pick only one MoveToDouble3D event
 */
class PickMoveToDouble3DEventReasoner(
    private val decisionData: DecisionData
) : DualUtilityReasoner() {

    private val movementEventKeyList: List<Int> =
        decisionData.universeData3DAtPlayer.getCurrentPlayerData(
        ).playerInternalData.eventDataMap.filter {
            // Filter out MoveToDouble3DEvent
            it.value.event.name == EventName.MOVE_TO_DOUBLE3D
        }.keys.toList()


    override fun getOptionList(): List<Option> {
        return movementEventKeyList.map {
            PickMoveToDouble3DEventOption(
                decisionData,
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
    private val decisionData: DecisionData,
    private val keepEventIndex: Int,
) : CommandListOption(decisionData) {

    private val movementEventMap: Map<Int, EventData> =
        decisionData.universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.eventDataMap.filter {
            // Filter out MoveToDouble3DEvent
            it.value.event.name == EventName.MOVE_TO_DOUBLE3D
        }

    override fun getCommandList(): List<Command> {
        return movementEventMap.filter { it.key != keepEventIndex }.map {
            SelectEventChoiceCommand(
                eventKey = it.key,
                eventName = it.value.event.name,
                choice = 1,
                fromId = decisionData.universeData3DAtPlayer.id,
                fromInt4D = decisionData.universeData3DAtPlayer.getCurrentPlayerData().int4D,
                toId = decisionData.universeData3DAtPlayer.id,
            )
        }
    }

    override fun getConsiderationList(): List<Consideration> {
        return if (movementEventMap.containsKey(keepEventIndex)) {
            listOf(
                RelationConsideration(
                    playerId = movementEventMap.getValue(keepEventIndex).event.fromId,
                    diplomacyData = decisionData.universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.diplomacyData(),
                )
            )
        } else {
            listOf()
        }
    }
}