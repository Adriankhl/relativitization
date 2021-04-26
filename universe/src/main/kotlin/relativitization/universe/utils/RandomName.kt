package relativitization.universe.utils

import io.github.serpro69.kfaker.Faker
import relativitization.universe.data.MutablePlayerInternalData
import relativitization.universe.data.PlayerInternalData

object RandomName {
    val faker = Faker()
    fun randomPlayerName(playerInternalData: PlayerInternalData): String {
        return faker.name.name()
    }
}