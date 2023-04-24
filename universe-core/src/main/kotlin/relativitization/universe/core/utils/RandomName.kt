package relativitization.universe.core.utils

import relativitization.universe.core.data.MutablePlayerInternalData
import relativitization.universe.core.data.PlayerInternalData

object RandomName {
    fun randomPlayerName(playerInternalData: PlayerInternalData): String {
        return "Player (${playerInternalData.aiName})"
    }

    fun randomPlayerName(playerInternalData: MutablePlayerInternalData): String {
        return "Player (${playerInternalData.aiName})"
    }
}