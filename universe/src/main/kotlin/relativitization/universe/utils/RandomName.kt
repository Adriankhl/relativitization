package relativitization.universe.utils

import io.github.serpro69.kfaker.Faker
import relativitization.universe.data.PlayerInternalData

object RandomName {
    val faker = Faker()
    fun randomPlayerName(playerInternalData: PlayerInternalData): String {
        val suffix: String = if (playerInternalData.leaderIdList.isEmpty()) {
            " (Leader)"
        } else {
            " (Subordinate)"
        }
        return faker.name.name() + suffix
    }
}