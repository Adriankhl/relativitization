package relativitization.universe.data

import kotlinx.serialization.Serializable

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
)

/**
 * Type of player: AI, human, none (e.g. resource)
 */
enum class PlayerType {
    AI,
    HUMAN,
    NONE,
}