package relativitization.universe.game.utils

import relativitization.universe.game.data.MutablePlayerInternalData
import relativitization.universe.game.data.PlayerInternalData

object RandomName {
    fun randomPlayerName(playerInternalData: PlayerInternalData): String {
        return "Player (${playerInternalData.aiName})"
    }

    fun randomPlayerName(playerInternalData: MutablePlayerInternalData): String {
        return "Player (${playerInternalData.aiName})"
    }
}