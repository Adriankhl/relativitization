package relativitization.universe.game.ai.defaults.node.self.event

import relativitization.universe.core.data.PlanDataAtPlayer
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.events.MutableEventData
import relativitization.universe.game.ai.defaults.consideration.diplomacy.RelationConsideration
import relativitization.universe.game.ai.defaults.consideration.event.HasMovementTargetConsideration
import relativitization.universe.game.ai.defaults.consideration.hierarchy.HierarchyRelationConsideration
import relativitization.universe.game.ai.defaults.utils.AINode
import relativitization.universe.game.ai.defaults.utils.DoNothingDualUtilityOption
import relativitization.universe.game.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.game.ai.defaults.utils.DualUtilityOption
import relativitization.universe.game.ai.defaults.utils.DualUtilityReasoner
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.game.ai.defaults.utils.SequenceReasoner
import relativitization.universe.game.data.commands.SelectEventChoiceCommand
import relativitization.universe.game.data.events.MoveToDouble3DEvent
import kotlin.random.Random

/**
 * Contains all reasoners related to movement events
 *
 * @property eventNameKeyMap a map from event name to a list of associated event key
 */
class MovementEventReasoner(
    private val eventNameKeyMap: Map<String, List<Int>>,
    private val random: Random,
) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        PickMoveToDouble3DEventReasoner(eventNameKeyMap, random),
    )
}

/**
 * Reasoner to pick only one MoveToDouble3D event
 */
class PickMoveToDouble3DEventReasoner(
    private val eventNameKeyMap: Map<String, List<Int>>,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        val movementEventKeySet: Set<Int> = eventNameKeyMap.getOrDefault(
            MoveToDouble3DEvent::class.keyName(),
            listOf()
        ).toSet()

        return movementEventKeySet.map {
            PickMoveToDouble3DEventDualUtilityOption(
                eventKey = it,
                otherEventKeySet = movementEventKeySet - it,
            )
        } + DoNothingDualUtilityOption(
            rank = 1,
            multiplier = 1.0,
            bonus = 1.0,
        )
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
                otherPlayerId = movementEventData.fromId,
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
                otherPlayerId = movementEventData.fromId,
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
                eventKey = it,
                choice = 1,
            )
        } + SelectEventChoiceCommand(
            toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
            eventKey = eventKey,
            choice = 0,
        )

        planDataAtPlayer.addAllCommand(commandList)
    }
}