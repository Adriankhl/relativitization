package relativitization.universe.data

import kotlinx.serialization.Serializable
import relativitization.universe.data.physics.Int4D

/**
 * Data of the basic unit (player)
 *
 * @param id playerId
 * @param playerType ai / human / none (e.g. resource)
 */
@Serializable
data class PlayerData(
    val id: Int,
    var playerType: PlayerType = PlayerType.AI,
    var int4D: Int4D = Int4D(0, 0, 0, 0),
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

@Serializable
data class PlayerInternalData(
    var topLeaderId: Int = -1
)