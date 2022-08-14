package relativitization.universe.ai.defaults.node.self.event

import relativitization.universe.ai.defaults.consideration.diplomacy.RelationConsideration
import relativitization.universe.ai.defaults.consideration.military.InDefensiveWarConsideration
import relativitization.universe.ai.defaults.consideration.military.WarLossConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.SelectEventChoiceCommand
import relativitization.universe.data.events.*
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.random.Random

/**
 * Contains all reasoners related to war
 *
 * @property eventNameKeyMap a map from event name to a list of associated event key
 */
class WarEventReasoner(
    private val eventNameKeyMap: Map<String, List<Int>>,
    private val random: Random,
) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        AllProposePeaceEventReasoner(eventNameKeyMap, random),
        AllCallAllyToWarEventReasoner(eventNameKeyMap, random),
        AllCallAllyToSubordinateWarEventReasoner(eventNameKeyMap, random),
    )
}

class AllProposePeaceEventReasoner(
    private val eventNameKeyMap: Map<String, List<Int>>,
    private val random: Random,
) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val eventKeyList: List<Int> = eventNameKeyMap.getOrDefault(
            ProposePeaceEvent::class.name(),
            listOf()
        )

        return eventKeyList.map {
            ProposePeaceEventReasoner(it, random)
        }
    }
}

class ProposePeaceEventReasoner(
    private val eventKey: Int,
    random: Random
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        AcceptPeaceOption(
            eventKey = eventKey,
        ),
        RejectPeaceOption(
            eventKey = eventKey,
        ),
    )
}

class AcceptPeaceOption(
    private val eventKey: Int,
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
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                eventKey = eventKey,
                eventName = ProposePeaceEvent::class.name(),
                choice = 1,
            )
        )
    }
}

class AllCallAllyToWarEventReasoner(
    private val eventNameKeyMap: Map<String, List<Int>>,
    private val random: Random,
) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val eventKeyList: List<Int> = eventNameKeyMap.getOrDefault(
            CallAllyToWarEvent::class.name(),
            listOf(),
        )

        return eventKeyList.map {
            CallAllyToWarEventReasoner(it, random)
        }
    }
}

class CallAllyToWarEventReasoner(
    private val eventKey: Int,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            AcceptAllyWarCallOption(eventKey),
            RejectAllyWarCallOption(eventKey),
        )
    }
}

class AcceptAllyWarCallOption(
    private val eventKey: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        val event: Event = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.eventDataMap.getValue(eventKey).event

        return if (event is CallAllyToWarEvent) {
            listOf(
                InDefensiveWarConsideration(
                    playerId = event.fromId,
                    warTargetId = event.warTargetId,
                    rankIfTrue = 2,
                    multiplierIfTrue = 1.0,
                    bonusIfTrue = 1.0,
                    rankIfFalse = 1,
                    multiplierIfFalse = 1.0,
                    bonusIfFalse = 0.5
                ),
                RelationConsideration(
                    otherPlayerId = event.fromId,
                    initialMultiplier = 1.0,
                    exponent = 1.05,
                    rank = 0,
                    bonus = 0.0
                ),
            )
        } else {
            logger.error("Event is not CallAllyToWarEvent")
            listOf()
        }
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        SelectEventChoiceCommand(
            toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
            fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
            fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
            eventKey = eventKey,
            eventName = CallAllyToWarEvent::class.name(),
            choice = 0,
        )
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

class RejectAllyWarCallOption(
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
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                eventKey = eventKey,
                eventName = CallAllyToWarEvent::class.name(),
                choice = 1,
            )
        )
    }
}

class AllCallAllyToSubordinateWarEventReasoner(
    private val eventNameKeyMap: Map<String, List<Int>>,
    private val random: Random,
) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val eventKeyList: List<Int> = eventNameKeyMap.getOrDefault(
            CallAllyToSubordinateWarEvent::class.name(),
            listOf(),
        )

        return eventKeyList.map {
            CallAllyToSubordinateWarEventReasoner(it, random)
        }
    }
}

class CallAllyToSubordinateWarEventReasoner(
    private val eventKey: Int,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            AcceptAllySubordinateWarCallOption(eventKey),
            RejectAllySubordinateWarCallOption(eventKey),
        )
    }
}

class AcceptAllySubordinateWarCallOption(
    private val eventKey: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        val event: Event = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.eventDataMap.getValue(eventKey).event

        return if (event is CallAllyToSubordinateWarEvent) {
            listOf(
                InDefensiveWarConsideration(
                    playerId = event.subordinateId,
                    warTargetId = event.warTargetId,
                    rankIfTrue = 1,
                    multiplierIfTrue = 1.0,
                    bonusIfTrue = 0.1,
                    rankIfFalse = 1,
                    multiplierIfFalse = 1.0,
                    bonusIfFalse = 0.01
                ),
                RelationConsideration(
                    otherPlayerId = event.fromId,
                    initialMultiplier = 1.0,
                    exponent = 1.05,
                    rank = 0,
                    bonus = 0.0
                ),
            )
        } else {
            logger.error("Event is not CallAllyToSubordinateWarEvent")
            listOf()
        }
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        SelectEventChoiceCommand(
            toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
            fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
            fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
            eventKey = eventKey,
            eventName = CallAllyToSubordinateWarEvent::class.name(),
            choice = 0,
        )
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

class RejectAllySubordinateWarCallOption(
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
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                eventKey = eventKey,
                eventName = CallAllyToSubordinateWarEvent::class.name(),
                choice = 1,
            )
        )
    }
}