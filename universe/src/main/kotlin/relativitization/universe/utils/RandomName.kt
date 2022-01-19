package relativitization.universe.utils

import relativitization.universe.data.PlayerInternalData

object RandomName {
    fun randomPlayerName(playerInternalData: PlayerInternalData): String {
        return "Player (${playerInternalData.aiName})"
    }
}