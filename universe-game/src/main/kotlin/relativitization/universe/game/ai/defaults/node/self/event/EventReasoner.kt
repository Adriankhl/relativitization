package relativitization.universe.game.ai.defaults.node.self.event

import relativitization.universe.game.ai.defaults.utils.AINode
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.game.ai.defaults.utils.SequenceReasoner
import relativitization.universe.game.data.PlanDataAtPlayer
import relativitization.universe.game.data.events.Event
import relativitization.universe.game.data.events.MutableEventData
import kotlin.random.Random
import kotlin.reflect.KClass

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
            eventDataMap.getValue(it).event.keyName()
        }

        return listOf(
            MovementEventReasoner(eventNameKeyMap, random),
            WarEventReasoner(eventNameKeyMap, random),
            AllianceEventReasoner(eventNameKeyMap, random),
        )
    }
}

fun Event.keyName(): String = this::class.simpleName.toString()

fun <T : Event> KClass<T>.keyName(): String = this.simpleName.toString()