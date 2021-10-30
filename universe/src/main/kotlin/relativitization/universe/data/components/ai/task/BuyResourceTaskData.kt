package relativitization.universe.data.components.ai.task

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.economy.ResourceType

/**
 * AI task: buy resource from player
 *
 * @property targetPlayerId the target player to buy from
 * @property targetResourceList the list of target resource, as a pair of type and class of resource
 */
@Serializable
data class BuyResourceTask(
    val targetPlayerId: Int = -1,
    val targetResourceList: List<Pair<ResourceType, Int>> = listOf(),
)

@Serializable
data class MutableBuyResourceTask(
    var targetPlayerId: Int = -1,
    val targetResourceList: MutableList<Pair<ResourceType, Int>> = mutableListOf()
)