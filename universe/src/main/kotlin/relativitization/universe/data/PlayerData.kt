package relativitization.universe.data

import kotlinx.serialization.Serializable
import org.apache.commons.lang3.mutable.Mutable
import relativitization.universe.data.commands.Command
import relativitization.universe.data.events.Event
import relativitization.universe.data.events.EventData
import relativitization.universe.data.events.MutableEventData
import relativitization.universe.data.physics.Int4D
import relativitization.universe.data.physics.MutableInt4D
import relativitization.universe.data.physics.MutablePhysicalData
import relativitization.universe.data.physics.PhysicalData

/**
 * Data of the basic unit (player)
 *
 * @property id playerId
 * @property playerType ai / human / none (e.g. resource)
 * @property int4D 4D coordinate of the player
 * @property attachedPlayerId the player which this player is currently attached to
 * @property playerInternalData the internal data of this player
 */
@Serializable
data class PlayerData(
    val id: Int,
    val playerType: PlayerType = PlayerType.AI,
    val int4D: Int4D = Int4D(0, 0, 0, 0),
    val attachedPlayerId: Int = -1,
    val playerInternalData: PlayerInternalData = PlayerInternalData()
)

@Serializable
data class MutablePlayerData(
    val id: Int,
    var playerType: PlayerType = PlayerType.AI,
    var int4D: MutableInt4D = MutableInt4D(0, 0, 0, 0),
    var attachedPlayerId: Int = -1,
    var playerInternalData: MutablePlayerInternalData = MutablePlayerInternalData()
)

/**
 * Type of player: AI, human, none (e.g. resource)
 */
enum class PlayerType {
    AI,
    HUMAN,
    NONE,
}

/**
 * Player internal data
 *
 * @property directLeaderId player id of the direct leader, equals -1 if no leader
 * @property subordinateIds list of player ids of the subordinates of this player
 * @property leaderIds list of player ids of leader, leader of leader, etc., from -1 to direct leader
 * @property isAlive whether the player is alive or dead
 */
@Serializable
data class PlayerInternalData(
    val directLeaderId: Int = -1,
    val directSubordinateIdList: List<Int> = listOf(),
    val leaderIdList: List<Int> = listOf(-1),
    val subordinateIdList: List<Int> = listOf(),
    val isAlive: Boolean = true,
    val eventDataList: List<EventData> = listOf(),
    val physicalData: PhysicalData = PhysicalData(),
)

@Serializable
data class MutablePlayerInternalData(
    var directLeaderId: Int = -1,
    var directSubordinateIdList: MutableList<Int> = mutableListOf(),
    var leaderIdList: MutableList<Int> = mutableListOf(-1),
    val subordinateIdList: MutableList<Int> = mutableListOf(),
    var isAlive: Boolean = true,
    var eventDataList: MutableList<MutableEventData> = mutableListOf(),
    var physicalData: MutablePhysicalData = MutablePhysicalData(),
)