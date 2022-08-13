package relativitization.universe.ai.defaults.node.self.event

import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.events.MutableEventData
import relativitization.universe.data.events.name
import kotlin.random.Random

class EventReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val eventDataMap: Map<Int, MutableEventData> = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.eventDataMap

        // Group event key by event name so that this is not needed by individual reasoners
        // Improve performance
        val eventNameKeyMap: Map<String, List<Int>> = eventDataMap.keys.groupBy {
            eventDataMap.getValue(it).event.name()
        }

        return listOf(
            MovementEventReasoner(eventNameKeyMap, random),
            WarEventReasoner(eventNameKeyMap, random),
            AllianceEventReasoner(eventNameKeyMap, random),
        )
    }
}