package relativitization.universe.data

import kotlinx.serialization.Serializable
import relativitization.universe.data.commands.Command
import relativitization.universe.data.physics.Int4D

/**
 * Data of the basic unit (player)
 *
 * @param id playerId
 * @param playerType ai / human / none (e.g. resource)
 * @param int4D 4D coordinate of the player
 * @param groupId which group of player does this player belong to in the current int4D grid
 * @param playerInternalData the internal data of this player
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
 * @param directLeaderId player id of the direct leader, equals -1 if no leader
 * @param subordinateIds list of player ids of the subordinates of this player
 * @param leaderIds list of player ids of leader, leader of leader, etc., from -1 to direct leader
 * @param isAlive whether the player is alive or dead
 * @param currentCommandList the command list of the current turn
 */
@Serializable
data class PlayerInternalData(
    var directLeaderId: Int = -1,
    var subordinateIds: MutableList<Int> = mutableListOf(),
    var leaderIds: MutableList<Int> = mutableListOf(-1),
    var isAlive: Boolean = true,
    var currentCommandList: MutableList<Command> = mutableListOf(),
)