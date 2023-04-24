package relativitization.universe.utils

import relativitization.universe.data.MutablePlayerInternalData
import relativitization.universe.data.PlayerInternalData

object RandomName {
    fun randomPlayerName(playerInternalData: PlayerInternalData): String {
        return "Player (${playerInternalData.aiName})"
    }

    fun randomPlayerName(playerInternalData: MutablePlayerInternalData): String {
        return "Player (${playerInternalData.aiName})"
    }
}