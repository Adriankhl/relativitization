package relativitization.universe.data

import kotlinx.serialization.Serializable
import relativitization.universe.data.commands.Command
import relativitization.universe.data.physics.Int4D

/**
 * Data of the basic unit (player)
 *
 * @property id playerId
 * @property playerType ai / human / none (e.g. resource)
 * @property int4D 4D coordinate of the player
 * @property groupId which group of player does this player belong to in the current int4D grid
 * @property playerInternalData the internal data of this player
 */
@Serializable
data class PlayerData(
    val id: Int,
    var playerType: PlayerType = PlayerType.AI,
    var int4D: Int4D = Int4D(0, 0, 0, 0),
    var groupId: Int = -1,
    var playerInternalData: PlayerInternalData = PlayerInternalData()
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
    var directLeaderId: Int = -1,
    var subordinateIdList: MutableList<Int> = mutableListOf(),
    var leaderIdList: MutableList<Int> = mutableListOf(-1),
    var isAlive: Boolean = true,
)